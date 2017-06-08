package com.avast.metrics.statsd;

import com.avast.metrics.api.*;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StatsDMetricsMonitor implements Monitor {
    protected final StatsDClient client;
    protected final String domain;
    protected final List<String> names = new ArrayList<>();
    protected final Naming naming;

    public StatsDMetricsMonitor(String host, int port, String domain, final Naming naming) {
        this.domain = domain;
        this.naming = naming;
        client = createStatsDClient(host, port, domain); // TODO use prefix?
    }

    protected NonBlockingStatsDClient createStatsDClient(final String host, final int port, final String domain) {
        return new NonBlockingStatsDClient(domain, host, port);
    }

    public StatsDMetricsMonitor(String host, int port, String domain) {
        this(host, port, domain, Naming.defaultNaming());
    }

    protected StatsDMetricsMonitor(StatsDMetricsMonitor monitor, String... newNames) {
        this.domain = monitor.domain;
        this.client = monitor.client;
        this.naming = monitor.naming;

        this.names.addAll(monitor.names);
        this.names.addAll(Arrays.asList(newNames));
    }

    @Override
    public StatsDMetricsMonitor named(final String name) {
        return new StatsDMetricsMonitor(this, name);
    }

    @Override
    public StatsDMetricsMonitor named(final String name1, final String name2, final String... restOfNames) {
        return new StatsDMetricsMonitor(named(name1).named(name2), restOfNames);
    }

    @Override
    public Meter newMeter(final String name) {
        return new StatsDMeter(client, constructMetricName(name));
    }

    @Override
    public Counter newCounter(final String name) {
        return null;  //TODO: implement
    }

    @Override
    public Timer newTimer(final String name) {
        return null;  //TODO: implement
    }

    @Override
    public TimerPair newTimerPair(final String name) {
        return null;  //TODO: implement
    }

    @Override
    public <T> Gauge<T> newGauge(final String name, final Supplier<T> gauge) {
        return null;  //TODO: implement
    }

    @Override
    public <T> Gauge<T> newGauge(final String name, final boolean replaceExisting, final Supplier<T> gauge) {
        return null;  //TODO: implement
    }

    @Override
    public Histogram newHistogram(final String name) {
        return null;  //TODO: implement
    }

    @Override
    public void remove(final Metric metric) {
        // noop
    }

    @Override
    public String getName() {
        return constructMetricName(Optional.empty());
    }

    @Override
    public void close() {
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
