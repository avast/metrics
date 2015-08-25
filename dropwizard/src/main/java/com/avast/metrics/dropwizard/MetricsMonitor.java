package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Counter;
import com.avast.metrics.api.Gauge;
import com.avast.metrics.api.Histogram;
import com.avast.metrics.api.Meter;
import com.avast.metrics.api.Metric;
import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Timer;
import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MetricsMonitor implements Monitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsMonitor.class);

    public static final String NAME_SEPARATOR = "/";

    protected final MetricRegistry registry;
    protected final List<String> names = new ArrayList<>();

    public MetricsMonitor() {
        this.registry = new MetricRegistry();
    }

    public MetricsMonitor(MetricRegistry registry) {
        this.registry = registry;
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
        return withMetricName(name, n -> new MetricsMeter(n, registry.meter(n)));
    }

    @Override
    public Counter newCounter(String name) {
        return withMetricName(name, n -> new MetricsCounter(n, registry.counter(n)));
    }

    @Override
    public Timer newTimer(String name) {
        return withMetricName(name, n -> new MetricsTimer(n, registry.timer(n)));
    }

    @Override
    public <T> Gauge<T> newGauge(String name, Supplier<T> gauge) {
        return withMetricName(name, n -> {
            MetricsGauge.SupplierGauge<T> supplierGauge = new MetricsGauge.SupplierGauge<>(gauge);
            registry.register(n, supplierGauge);
            return new MetricsGauge<>(n, supplierGauge);
        });
    }

    @Override
    public Histogram newHistogram(String name) {
        return withMetricName(name, n -> new MetricsHistogram(n, registry.histogram(n)));
    }

    @Override
    public void remove(Metric metric) {
        registry.remove(metric.getName());
    }

    protected <T> T withMetricName(String name, Function<String, T> metricCreator) {
        String finalName = constructMetricName(name);

        if (LOGGER.isDebugEnabled()) {
            String nameForLogging = finalName.replaceAll(Pattern.quote(separator()), "/");
            LOGGER.debug("Creating metric '{}'", nameForLogging);

            if (registry.getNames().contains(finalName)) {
                LOGGER.debug("Metric '{}' is already in the registry, check if the duplication is fine.", nameForLogging);
            }
        }

        return metricCreator.apply(finalName);
    }

    protected String separator() {
        return NAME_SEPARATOR;
    }

    protected String constructMetricName(String finalName) {
        return constructMetricName(Optional.ofNullable(finalName));
    }

    protected String constructMetricName(Optional<String> finalName) {
        List<String> copy = new ArrayList<>(names);
        finalName.ifPresent(copy::add);
        return copy.stream().collect(Collectors.joining(separator()));
    }

}
