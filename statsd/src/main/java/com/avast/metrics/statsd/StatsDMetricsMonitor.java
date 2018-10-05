package com.avast.metrics.statsd;

import com.avast.metrics.TimerPairImpl;
import com.avast.metrics.api.*;
import com.avast.metrics.api.Timer;
import com.avast.metrics.filter.FilterConfig;
import com.avast.metrics.filter.MetricsFilter;
import com.avast.metrics.test.NoOpMonitor;
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

@SuppressWarnings({"WeakerAccess", "OptionalUsedAsFieldOrParameterType"})
public class StatsDMetricsMonitor implements Monitor {
    protected final StatsDClient client;
    protected final String prefix;
    protected final List<String> names = new ArrayList<>();
    protected final Naming naming;
    protected final ScheduledExecutorService scheduler;
    protected final Duration gaugeSendPeriod;
    protected final MetricsFilter metricsFilter;
    protected final boolean autoRegisterMetric;

    protected final Map<String, ScheduledFuture<?>> gauges = new HashMap<>();

    public StatsDMetricsMonitor(String host, int port, boolean autoRegisterMetrics, String prefix, final Naming naming, final Duration gaugeSendPeriod, final ScheduledExecutorService scheduler, MetricsFilter metricsFilter) {
        this.prefix = prefix;
        this.naming = naming;
        this.gaugeSendPeriod = gaugeSendPeriod;
        this.scheduler = scheduler;
        client = createStatsDClient(host, port, prefix);
        this.metricsFilter = metricsFilter;
        this.autoRegisterMetric = autoRegisterMetrics;
    }


    public StatsDMetricsMonitor(String host, int port, String prefix, final Naming naming, final Duration gaugeSendPeriod, final ScheduledExecutorService scheduler, MetricsFilter metricsFilter) {
        this(host, port, false, prefix, naming, gaugeSendPeriod, scheduler, metricsFilter);
    }

    public StatsDMetricsMonitor(String host, int port, String prefix, final Naming naming, final Duration gaugeSendPeriod, final ScheduledExecutorService scheduler) {
        this(host, port, prefix, naming, gaugeSendPeriod, scheduler, MetricsFilter.ALL_ENABLED);
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


    public StatsDMetricsMonitor(String host, int port, String prefix, MetricsFilter metricsFilter) {
        this(host, port, prefix, Naming.defaultNaming(), getDefaultGaugeSendPeriod(), createScheduler(), metricsFilter);
    }

    protected StatsDMetricsMonitor(StatsDMetricsMonitor monitor, final Duration gaugeSendPeriod, final ScheduledExecutorService scheduler, String... newNames) {
        this(monitor, gaugeSendPeriod, scheduler, MetricsFilter.ALL_ENABLED, newNames);
    }

    protected StatsDMetricsMonitor(StatsDMetricsMonitor monitor, final Duration gaugeSendPeriod, final ScheduledExecutorService scheduler, MetricsFilter metricsFilter, String... newNames) {
        this.prefix = monitor.prefix;
        this.client = monitor.client;
        this.naming = monitor.naming;
        this.scheduler = scheduler;
        this.gaugeSendPeriod = gaugeSendPeriod;
        this.metricsFilter = metricsFilter;
        this.autoRegisterMetric = monitor.isAutoRegisterMetric();
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
        return new StatsDMetricsMonitor(this, gaugeSendPeriod, scheduler, metricsFilter, name);
    }

    @Override
    public StatsDMetricsMonitor named(final String name1, final String name2, final String... restOfNames) {
        return new StatsDMetricsMonitor(named(name1).named(name2), gaugeSendPeriod, scheduler, metricsFilter, restOfNames);
    }

    @Override
    public Meter newMeter(final String name) {
        final String metricName = constructMetricName(name);
        FilterConfig config = metricsFilter.getConfig(metricName);

        if (config.isEnabled()) {
            return init(new StatsDMeter(client, metricName, config.getSampleRate()));
        } else {
            return NoOpMonitor.INSTANCE.newMeter(metricName);
        }
    }

    @Override
    public Counter newCounter(final String name) {
        final String metricName = constructMetricName(name);
        FilterConfig config = metricsFilter.getConfig(metricName);

        if (config.isEnabled()) {
            return init(new StatsDCounter(client, metricName, config.getSampleRate()));
        } else {
            return NoOpMonitor.INSTANCE.newCounter(metricName);
        }
    }

    @Override
    public Timer newTimer(final String name) {
        final String metricName = constructMetricName(name);
        FilterConfig config = metricsFilter.getConfig(metricName);

        if (config.isEnabled()) {
            return init(new StatsDTimer(client, metricName, config.getSampleRate()));
        } else {
            return NoOpMonitor.INSTANCE.newTimer(metricName);
        }

    }

    @Override
    public TimerPair newTimerPair(final String name) {
        return new TimerPairImpl(
                newTimer(naming.successTimerName(name)),
                newTimer(naming.failureTimerName(name))
        );
    }

    @Override
    public <T> Gauge<T> newGauge(final String name, final Supplier<T> gauge) {
        return newGauge(name, false, gauge);
    }

    @Override
    public <T> Gauge<T> newGauge(final String name, final boolean replaceExisting, final Supplier<T> supplier) {
        final String metricName = constructMetricName(name);
        FilterConfig config = metricsFilter.getConfig(metricName);

        if (config.isEnabled()) {
            synchronized (gauges) {
                final ScheduledFuture<?> existing = gauges.get(metricName);

                if (existing != null) {
                    if (!replaceExisting)
                        throw new IllegalStateException("Gauge with name '" + name + "' is already registered");

                    existing.cancel(false);
                }

                final StatsDGauge<T> gauge = init(new StatsDGauge<>(client, metricName, supplier, config.getSampleRate()));

                final ScheduledFuture<?> scheduled = scheduler.scheduleAtFixedRate(gauge::send, 0, gaugeSendPeriod.toMillis(), TimeUnit.MILLISECONDS);

                gauges.put(name, scheduled);

                return gauge;
            }
        } else {
            return NoOpMonitor.INSTANCE.newGauge(metricName, replaceExisting, supplier);
        }
    }

    @Override
    public Histogram newHistogram(final String name) {
        final String metricName = constructMetricName(name);
        FilterConfig config = metricsFilter.getConfig(metricName);

        if (config.isEnabled()) {
            return init(new StatsDHistogram(client, metricName));
        } else {
            return NoOpMonitor.INSTANCE.newHistogram(metricName);
        }
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

    public boolean isAutoRegisterMetric() {
        return autoRegisterMetric;
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

    private <A extends StatsDMetric> A init(A a) {
        if (autoRegisterMetric) {
            a.init();
            return a;
        } else {
            return a;
        }
    }
}
