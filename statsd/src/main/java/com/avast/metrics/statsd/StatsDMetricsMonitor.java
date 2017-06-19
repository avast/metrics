package com.avast.metrics.statsd;

import com.avast.metrics.TimerPairImpl;
import com.avast.metrics.api.Metric;
import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Naming;
import com.avast.metrics.api.TimerPair;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "OptionalUsedAsFieldOrParameterType", "unused"})
public class StatsDMetricsMonitor implements Monitor {
    protected final StatsDClient client;
    protected final String prefix;
    protected final List<String> names = new ArrayList<>();
    protected final Naming naming;
    protected final ScheduledExecutorService scheduler;
    protected final Duration gaugeSendPeriod;

    protected final Map<String, ScheduledFuture<?>> gauges = new HashMap<>();

    public StatsDMetricsMonitor(String host, int port, String prefix, final Naming naming, final Duration gaugeSendPeriod, final ScheduledExecutorService scheduler) {
        this.prefix = prefix;
        this.naming = naming;
        this.gaugeSendPeriod = gaugeSendPeriod;
        this.scheduler = scheduler;
        client = createStatsDClient(host, port, prefix);
    }

    public StatsDMetricsMonitor(String host, int port, String prefix, final Naming naming) {
        this(host, port, prefix, naming, getDefaultGaugeSendPeriod(), createScheduler());
    }

    public StatsDMetricsMonitor(String host, int port, String prefix, final Duration gaugeSendPeriod, final ScheduledExecutorService scheduler) {
        this(host, port, prefix, Naming.defaultNaming(), gaugeSendPeriod, scheduler);
    }

    public StatsDMetricsMonitor(String host, int port, String prefix) {
        this(host, port, prefix, Naming.defaultNaming(), getDefaultGaugeSendPeriod(), createScheduler());
    }

    protected StatsDMetricsMonitor(StatsDMetricsMonitor monitor, final Duration gaugeSendPeriod, final ScheduledExecutorService scheduler, String... newNames) {
        this.prefix = monitor.prefix;
        this.client = monitor.client;
        this.naming = monitor.naming;
        this.scheduler = scheduler;
        this.gaugeSendPeriod = gaugeSendPeriod;

        this.names.addAll(monitor.names);
        this.names.addAll(Arrays.asList(newNames));
    }

    protected StatsDClient createStatsDClient(final String host, final int port, final String prefix) {
        return new NonBlockingStatsDClient(prefix, host, port);
    }

    private static Duration getDefaultGaugeSendPeriod() {
        return Duration.ofSeconds(1);
    }

    private static ScheduledExecutorService createScheduler() {
        return Executors.newScheduledThreadPool(2);
    }

    @Override
    public StatsDMetricsMonitor named(final String name) {
        return new StatsDMetricsMonitor(this, gaugeSendPeriod, scheduler, name);
    }

    @Override
    public StatsDMetricsMonitor named(final String name1, final String name2, final String... restOfNames) {
        return new StatsDMetricsMonitor(named(name1).named(name2), gaugeSendPeriod, scheduler, restOfNames);
    }

    @Override
    public StatsDMeter newMeter(final String name) {
        return new StatsDMeter(client, constructMetricName(name));
    }

    @Override
    public StatsDCounter newCounter(final String name) {
        return new StatsDCounter(client, constructMetricName(name));
    }

    @Override
    public StatsDTimer newTimer(final String name) {
        return new StatsDTimer(client, constructMetricName(name));
    }

    @Override
    public TimerPair newTimerPair(final String name) {
        return new TimerPairImpl(
                newTimer(naming.successTimerName(name)),
                newTimer(naming.failureTimerName(name))
        );
    }

    @Override
    public <T> StatsDGauge<T> newGauge(final String name, final Supplier<T> gauge) {
        return newGauge(name, false, gauge);
    }

    @Override
    public <T> StatsDGauge<T> newGauge(final String name, final boolean replaceExisting, final Supplier<T> supplier) {
        final String finalName = constructMetricName(name);

        synchronized (gauges) {
            final ScheduledFuture<?> existing = gauges.get(finalName);

            if (existing != null) {
                if (!replaceExisting) throw new IllegalStateException("Gauge with name '" + name + "' is already registered");

                existing.cancel(false);
            }

            final StatsDGauge<T> gauge = new StatsDGauge<>(client, finalName, supplier);

            final ScheduledFuture<?> scheduled = scheduler.scheduleAtFixedRate(gauge::send, 0, gaugeSendPeriod.toMillis(), TimeUnit.MILLISECONDS);

            gauges.put(name, scheduled);

            return gauge;
        }
    }

    @Override
    public StatsDHistogram newHistogram(final String name) {
        return new StatsDHistogram(client, constructMetricName(name));
    }

    @Override
    public void remove(final Metric metric) {
        final ScheduledFuture<?> removed = gauges.remove(metric.getName());

        if (removed != null) {
            removed.cancel(false);
        }

        // no-op for other types
    }

    @Override
    public String getName() {
        return constructMetricName(Optional.empty());
    }

    @Override
    public void close() {
        scheduler.shutdown();
        client.stop();
    }

    protected String constructMetricName(String finalName) {
        return constructMetricName(Optional.ofNullable(finalName));
    }

    protected String constructMetricName(Optional<String> finalName) {
        List<String> copy = new ArrayList<>(names);
        finalName.ifPresent(copy::add);
        return copy.stream().collect(Collectors.joining("."));
    }
}
