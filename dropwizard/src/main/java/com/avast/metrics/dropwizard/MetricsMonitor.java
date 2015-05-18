package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Counter;
import com.avast.metrics.api.Gauge;
import com.avast.metrics.api.Histogram;
import com.avast.metrics.api.Meter;
import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Timer;
import com.codahale.metrics.MetricRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MetricsMonitor implements Monitor {

    public static final String NAME_SEPARATOR = "/";

    protected final MetricRegistry registry;
    protected final List<String> names = new ArrayList<>();

    public MetricsMonitor() {
        this.registry = new MetricRegistry();
    }

    protected MetricsMonitor(MetricsMonitor original, String name) {
        this.registry = original.registry;
        this.names.addAll(original.names);
        this.names.add(name);
    }

    @Override
    public Monitor named(String name) {
        return new MetricsMonitor(this, name);
    }

    @Override
    public String getName() {
        return constructMetricName(Optional.empty());
    }

    @Override
    public Meter newMeter(String name) {
        return new MetricsMeter(registry.meter(constructMetricName(name)));
    }

    @Override
    public Counter newCounter(String name) {
        return new MetricsCounter(registry.counter(constructMetricName(name)));
    }

    @Override
    public Timer newTimer(String name) {
        return new MetricsTimer(registry.timer(constructMetricName(name)));
    }

    @Override
    public <T> Gauge<T> newGauge(String name, Supplier<T> gauge) {
        MetricsGauge.SupplierGauge<T> supplierGauge = new MetricsGauge.SupplierGauge<>(gauge);
        registry.register(constructMetricName(name), supplierGauge);
        return new MetricsGauge<>(supplierGauge);
    }

    @Override
    public Histogram newHistogram(String name) {
        return new MetricsHistogram(registry.histogram(constructMetricName(name)));
    }

    protected String constructMetricName(String finalName) {
        return constructMetricName(Optional.ofNullable(finalName));
    }

    protected String constructMetricName(Optional<String> finalName) {
        List<String> copy = new ArrayList<>(names);
        finalName.ifPresent(copy::add);
        return copy.stream().collect(Collectors.joining(NAME_SEPARATOR));
    }

}
