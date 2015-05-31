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
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MetricsMonitor implements Monitor {

    public static final String NAME_SEPARATOR = "/";

    protected final MetricRegistry registry;
    protected final List<String> names = new ArrayList<>();
    protected final boolean generateUniqueNames;
    protected final Random random;

    public MetricsMonitor(boolean generateUniqueNames) {
        this.registry = new MetricRegistry();
        this.generateUniqueNames = generateUniqueNames;
        this.random = new Random();
    }

    public MetricsMonitor() {
        this(false);
    }

    protected MetricsMonitor(MetricsMonitor original, String name) {
        this.registry = original.registry;
        this.names.addAll(original.names);
        this.names.add(name);
        this.generateUniqueNames = original.generateUniqueNames;
        this.random = original.random;
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
        return checkUniqueName(name, n -> new MetricsMeter(n, registry.meter(n)));
    }

    @Override
    public Counter newCounter(String name) {
        return checkUniqueName(name, n -> new MetricsCounter(n, registry.counter(n)));
    }

    @Override
    public Timer newTimer(String name) {
        return checkUniqueName(name, n -> new MetricsTimer(n, registry.timer(n)));
    }

    @Override
    public <T> Gauge<T> newGauge(String name, Supplier<T> gauge) {
        return checkUniqueName(name, n -> {
            MetricsGauge.SupplierGauge<T> supplierGauge = new MetricsGauge.SupplierGauge<>(gauge);
            registry.register(n, supplierGauge);
            return new MetricsGauge<>(n, supplierGauge);
        });
    }

    @Override
    public Histogram newHistogram(String name) {
        return checkUniqueName(name, n -> new MetricsHistogram(n, registry.histogram(n)));
    }

    protected <T> T checkUniqueName(String name, Function<String, T> metricCreator) {
        String finalName = constructMetricName(name);
        if (registry.getNames().contains(finalName)) {
            if (generateUniqueNames) {
                String generatedName = finalName + random.nextInt();
                return metricCreator.apply(generatedName);
            } else {
                throw new IllegalArgumentException("Metric name " + finalName + " is not unique!");
            }
        } else {
            return metricCreator.apply(finalName);
        }
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
