package com.avast.metrics.dropwizard.formatting;

import com.avast.metrics.api.*;
import com.avast.metrics.dropwizard.MetricsMonitor;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Metrics monitor that formats names and values of the monitored objects.
 */
public class FormattingMetricsMonitor extends MetricsMonitor {
    private final Formatter formatter;

    public FormattingMetricsMonitor(Formatter formatter) {
        this(new MetricRegistry(), new GraphiteNaming(), formatter);
    }

    public FormattingMetricsMonitor(MetricRegistry metricRegistry, Naming naming, Formatter formatter) {
        super(metricRegistry, naming);
        this.formatter = formatter;
    }

    private FormattingMetricsMonitor(FormattingMetricsMonitor original, String... names) {
        super(original, names);
        this.formatter = original.formatter;
    }

    @Override
    public FormattingMetricsMonitor named(String name) {
        return new FormattingMetricsMonitor(this, formatter.sanitizeName(name));
    }

    @Override
    public FormattingMetricsMonitor named(String name1, String name2, String... restOfNames) {
        String[] allNames = new String[restOfNames.length + 2];
        allNames[0] = name1;
        allNames[1] = name2;
        System.arraycopy(restOfNames, 0, allNames, 2, restOfNames.length);

        String[] sanitizedNames = Arrays.stream(allNames)
                .map(formatter::sanitizeName)
                .collect(Collectors.toList())
                .toArray(new String[restOfNames.length]);

        return new FormattingMetricsMonitor(this, sanitizedNames);
    }

    @Override
    public Meter newMeter(String name) {
        return super.newMeter(formatter.sanitizeName(name));
    }

    @Override
    public Counter newCounter(String name) {
        return super.newCounter(formatter.sanitizeName(name));
    }

    @Override
    public Timer newTimer(String name) {
        return super.newTimer(formatter.sanitizeName(name));
    }

    @Override
    public TimerPair newTimerPair(String name) {
        return super.newTimerPair(formatter.sanitizeName(name));
    }

    @Override
    public <T> Gauge<T> newGauge(String name, Supplier<T> gauge) {
        return super.newGauge(formatter.sanitizeName(name), gauge);
    }

    @Override
    public <T> Gauge<T> newGauge(String name, boolean replaceExisting, Supplier<T> gauge) {
        return super.newGauge(formatter.sanitizeName(name), replaceExisting, gauge);
    }

    @Override
    public Histogram newHistogram(String name) {
        return super.newHistogram(formatter.sanitizeName(name));
    }

    @Override
    protected String separator() {
        return formatter.nameSeparator();
    }

    /**
     * Implementation in parent class can be considered broken from contract point of view. It uses hardcoded "/" as a
     * separator. The unexpected behavior is needed by JmxMetricsMonitor that internally uses group of unusual characters.
     *
     * TODO: Define expected behavior by writing contract of MetricsMonitor.getName(), optionally refactor all related code.
     */
    @Override
    public String getName() {
        return constructMetricName(Optional.empty(), separator());
    }

    public String format() {
        Stream<MetricValue> metrics = registry.getMetrics()
                .entrySet()
                .stream()
                .flatMap(entry -> toMetricValue(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(MetricValue::getName));

        return formatter.format(metrics);
    }

    private Stream<MetricValue> toMetricValue(String name, com.codahale.metrics.Metric metric) {
        if (metric instanceof com.codahale.metrics.Counter) {
            return mapCounter(name, (com.codahale.metrics.Counter) metric);
        } else if (metric instanceof com.codahale.metrics.Gauge) {
            return mapGauge(name, (com.codahale.metrics.Gauge) metric);
        } else if (metric instanceof com.codahale.metrics.Meter) {
            return mapMeter(name, (com.codahale.metrics.Meter) metric);
        } else if (metric instanceof com.codahale.metrics.Histogram) {
            return mapHistogram(name, (com.codahale.metrics.Histogram) metric);
        } else if (metric instanceof com.codahale.metrics.Timer) {
            return mapTimer(name, (com.codahale.metrics.Timer) metric);
        } else {
            LOGGER.error("Unexpected metric class: {}", metric.getClass());
            return Stream.empty();
        }
    }

    private Stream<MetricValue> mapCounter(String name, com.codahale.metrics.Counter counter) {
        return Stream.of(
                new MetricValue(naming.countName(name), formatter.formatNumber(counter.getCount()))
        );
    }

    private Stream<MetricValue> mapGauge(String name, com.codahale.metrics.Gauge gauge) {
        return Stream.of(
                new MetricValue(name, formatter.formatObject(gauge.getValue()))
        );
    }

    private Stream<MetricValue> mapMeter(String name, com.codahale.metrics.Meter meter) {
        return Stream.of(
                new MetricValue(naming.countName(name), formatter.formatNumber(meter.getCount())),
                new MetricValue(naming.meanName(name), formatter.formatNumber(meter.getMeanRate())),
                new MetricValue(naming.oneMinuteRateName(name), formatter.formatNumber(meter.getOneMinuteRate())),
                new MetricValue(naming.fiveMinuteRateName(name), formatter.formatNumber(meter.getFiveMinuteRate())),
                new MetricValue(naming.fifteenMinuteRateName(name), formatter.formatNumber(meter.getFifteenMinuteRate()))
        );
    }

    private Stream<MetricValue> mapHistogram(String name, com.codahale.metrics.Histogram histogram) {
        Snapshot snapshot = histogram.getSnapshot();

        return Stream.of(
                new MetricValue(naming.countName(name), formatter.formatNumber(histogram.getCount())),
                new MetricValue(naming.minName(name), formatter.formatNumber(snapshot.getMin())),
                new MetricValue(naming.maxName(name), formatter.formatNumber(snapshot.getMax())),
                new MetricValue(naming.meanName(name), formatter.formatNumber(snapshot.getMean())),
                new MetricValue(naming.stdDevName(name), formatter.formatNumber(snapshot.getStdDev())),

                // TODO: Whatever percentile can be added
                new MetricValue(naming.percentileName(name, 50), formatter.formatNumber(snapshot.getValue(0.5)))
        );
    }

    private Stream<MetricValue> mapTimer(String name, com.codahale.metrics.Timer timer) {
        Snapshot snapshot = timer.getSnapshot();

        return Stream.of(
                new MetricValue(naming.countName(name), formatter.formatNumber(timer.getCount())),

                new MetricValue(naming.meanName(name), formatter.formatNumber(timer.getMeanRate())),
                new MetricValue(naming.oneMinuteRateName(name), formatter.formatNumber(timer.getOneMinuteRate())),
                new MetricValue(naming.fiveMinuteRateName(name), formatter.formatNumber(timer.getFiveMinuteRate())),
                new MetricValue(naming.fifteenMinuteRateName(name), formatter.formatNumber(timer.getFifteenMinuteRate())),

                new MetricValue(naming.minName(name), formatter.formatNumber(snapshot.getMin())),
                new MetricValue(naming.maxName(name), formatter.formatNumber(snapshot.getMax())),
                new MetricValue(naming.meanName(name), formatter.formatNumber(snapshot.getMean())),
                new MetricValue(naming.stdDevName(name), formatter.formatNumber(snapshot.getStdDev())),

                // TODO: Whatever percentile can be added
                new MetricValue(naming.percentileName(name, 50), formatter.formatNumber(snapshot.getValue(0.5)))
        );
    }
}
