package com.avast.metrics.dropwizard.formatting;

import com.avast.metrics.api.*;
import com.avast.metrics.api.Timer;
import com.avast.metrics.dropwizard.MetricsMonitor;
import com.avast.metrics.dropwizard.formatting.fields.FieldsFormatting;
import com.avast.metrics.dropwizard.formatting.fields.HistogramFormatting;
import com.avast.metrics.dropwizard.formatting.fields.MeterFormatting;
import com.avast.metrics.dropwizard.formatting.fields.TimerFormatting;
import com.avast.metrics.filter.MetricsFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Metrics monitor that formats names and values of the monitored objects.
 */
@SuppressWarnings("WeakerAccess")
public class FormattingMetricsMonitor extends MetricsMonitor {
    private final Formatter formatter;

    /**
     * Constant prefix that is prepended to names of all metrics but is not used for any filtering logic.
     * The parts may be for example: environment, datacenter, application and instance.
     */
    private final String namePrefix;

    public FormattingMetricsMonitor(Formatter formatter, List<String> namePrefix) {
        this(new MetricRegistry(), Naming.defaultNaming(), formatter, namePrefix);
    }

    public FormattingMetricsMonitor(MetricRegistry metricRegistry, Naming naming, Formatter formatter, List<String> namePrefix) {
        super(metricRegistry, naming);
        this.formatter = formatter;

        if (namePrefix.isEmpty()) {
            this.namePrefix = "";
        } else {
            this.namePrefix = namePrefix
                    .stream()
                    .map(formatter::sanitizeName)
                    .collect(Collectors.joining(formatter.nameSeparator(), "", formatter.nameSeparator()));
        }
    }

    private FormattingMetricsMonitor(FormattingMetricsMonitor original, String... names) {
        super(original, names);
        this.formatter = original.formatter;
        this.namePrefix = original.namePrefix;
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
     * <p>
     * TODO: Define expected behavior by writing contract of MetricsMonitor.getName(), optionally refactor all related code.
     */
    @Override
    public String getName() {
        return constructMetricName(Optional.empty(), separator());
    }

    public String contentType() {
        return formatter.contentType();
    }

    public String format(MetricsFilter filter, FieldsFormatting fieldsFormatting) {
        Stream<MetricValue> metrics = registry.getMetrics()
                .entrySet()
                .stream()
                .filter(entry -> filter.isEnabled(entry.getKey()))
                .flatMap(entry -> toMetricValue(entry.getKey(), entry.getValue(), fieldsFormatting))
                .sorted(Comparator.comparing(MetricValue::getName));

        return formatter.format(metrics);
    }

    private Stream<MetricValue> toMetricValue(String name, com.codahale.metrics.Metric metric,
                                              FieldsFormatting fieldsFormatting) {
        String prefixedName = namePrefix + name;

        if (metric instanceof com.codahale.metrics.Counter) {
            return mapCounter(prefixedName, (com.codahale.metrics.Counter) metric, fieldsFormatting);
        } else if (metric instanceof com.codahale.metrics.Gauge) {
            return mapGauge(prefixedName, (com.codahale.metrics.Gauge) metric, fieldsFormatting);
        } else if (metric instanceof com.codahale.metrics.Meter) {
            return mapMeter(prefixedName, (com.codahale.metrics.Meter) metric, fieldsFormatting);
        } else if (metric instanceof com.codahale.metrics.Histogram) {
            return mapHistogram(prefixedName, (com.codahale.metrics.Histogram) metric, fieldsFormatting);
        } else if (metric instanceof com.codahale.metrics.Timer) {
            return mapTimer(prefixedName, (com.codahale.metrics.Timer) metric, fieldsFormatting);
        } else {
            LOGGER.error("Unexpected metric class: {}", metric.getClass());
            return Stream.empty();
        }
    }

    private Stream<MetricValue> mapCounter(String name, com.codahale.metrics.Counter counter,
                                           FieldsFormatting fieldsFormatting) {
        if (fieldsFormatting.getCounter().isCount()) {
            return Stream.of(new MetricValue(appendName(name, "count"), formatter.formatNumber(counter.getCount())));
        } else {
            return Stream.empty();
        }
    }

    private Stream<MetricValue> mapGauge(String name, com.codahale.metrics.Gauge gauge,
                                         FieldsFormatting fieldsFormatting) {
        if (fieldsFormatting.getGauge().isValue()) {
            return Stream.of(new MetricValue(appendName(name, "value"), formatter.formatObject(gauge.getValue())));
        } else {
            return Stream.empty();
        }
    }

    private Stream<MetricValue> mapMeter(String name, com.codahale.metrics.Meter meter,
                                         FieldsFormatting fieldsFormatting) {
        MeterFormatting meterFormatting = fieldsFormatting.getMeter();
        List<MetricValue> values = new LinkedList<>();

        if (meterFormatting.isCount())
            values.add(new MetricValue(appendName(name, "count"), formatter.formatNumber(meter.getCount())));
        if (meterFormatting.isMean())
            values.add(new MetricValue(appendName(name, "ratemean"), formatter.formatNumber(meter.getMeanRate())));
        if (meterFormatting.isOneMinuteRate())
            values.add(new MetricValue(appendName(name, "rate1m"), formatter.formatNumber(meter.getOneMinuteRate())));
        if (meterFormatting.isFiveMinuteRate())
            values.add(new MetricValue(appendName(name, "rate5m"), formatter.formatNumber(meter.getFiveMinuteRate())));
        if (meterFormatting.isFifteenMinuteRate())
            values.add(new MetricValue(appendName(name, "rate15m"), formatter.formatNumber(meter.getFifteenMinuteRate())));

        return values.stream();
    }

    private Stream<MetricValue> mapHistogram(String name, com.codahale.metrics.Histogram histogram,
                                             FieldsFormatting fieldsFormatting) {
        HistogramFormatting histogramFormatting = fieldsFormatting.getHistogram();
        Snapshot snapshot = histogram.getSnapshot();
        List<MetricValue> values = new LinkedList<>();

        if (histogramFormatting.isCount())
            values.add(new MetricValue(appendName(name, "count"), formatter.formatNumber(histogram.getCount())));
        if (histogramFormatting.isMin())
            values.add(new MetricValue(appendName(name, "min"), formatter.formatNumber(snapshot.getMin())));
        if (histogramFormatting.isMax())
            values.add(new MetricValue(appendName(name, "max"), formatter.formatNumber(snapshot.getMax())));
        if (histogramFormatting.isMean())
            values.add(new MetricValue(appendName(name, "mean"), formatter.formatNumber(snapshot.getMean())));
        if (histogramFormatting.isStdDev())
            values.add(new MetricValue(appendName(name, "stddev"), formatter.formatNumber(snapshot.getStdDev())));

        histogramFormatting
                .getPercentiles()
                .forEach(percentile ->
                        values.add(new MetricValue(appendName(name, percentileName(percentile)),
                                formatter.formatNumber(snapshot.getValue(percentile)))));

        return values.stream();
    }

    private Stream<MetricValue> mapTimer(String name, com.codahale.metrics.Timer timer, FieldsFormatting fieldsFormatting) {
        TimerFormatting timerFormatting = fieldsFormatting.getTimer();
        Snapshot snapshot = timer.getSnapshot();
        List<MetricValue> values = new LinkedList<>();

        if (timerFormatting.isCount())
            values.add(new MetricValue(appendName(name, "count"), formatter.formatNumber(timer.getCount())));
        if (timerFormatting.isMean())
            values.add(new MetricValue(appendName(name, "ratemean"), formatter.formatNumber(timer.getMeanRate())));
        if (timerFormatting.isOneMinuteRate())
            values.add(new MetricValue(appendName(name, "rate1m"), formatter.formatNumber(timer.getOneMinuteRate())));
        if (timerFormatting.isFiveMinuteRate())
            values.add(new MetricValue(appendName(name, "rate5m"), formatter.formatNumber(timer.getFiveMinuteRate())));
        if (timerFormatting.isFifteenMinuteRate())
            values.add(new MetricValue(appendName(name, "rate15m"), formatter.formatNumber(timer.getFifteenMinuteRate())));
        if (timerFormatting.isMin())
            values.add(new MetricValue(appendName(name, "min"), formatter.formatNumber(ns2ms(snapshot.getMin()))));
        if (timerFormatting.isMax())
            values.add(new MetricValue(appendName(name, "max"), formatter.formatNumber(ns2ms(snapshot.getMax()))));
        if (timerFormatting.isMean())
            values.add(new MetricValue(appendName(name, "mean"), formatter.formatNumber(ns2ms(snapshot.getMean()))));
        if (timerFormatting.isStdDev())
            values.add(new MetricValue(appendName(name, "stddev"), formatter.formatNumber(ns2ms(snapshot.getStdDev()))));

        timerFormatting
                .getPercentiles()
                .forEach(percentile ->
                        values.add(new MetricValue(appendName(name, percentileName(percentile)),
                                formatter.formatNumber(ns2ms(snapshot.getValue(percentile))))));

        return values.stream();
    }

    private String percentileName(Double percentile) {
        double percent = percentile * 100;

        // https://stackoverflow.com/questions/15963895/how-to-check-if-a-double-value-has-no-decimal-part
        if (percent % 1 == 0) {
            return formatter.sanitizeName(String.format(Locale.ENGLISH, "p%.0f", percent));
        } else {
            return formatter.sanitizeName(String.format(Locale.ENGLISH, "p%s", percent));
        }
    }

    private String appendName(String base, String part) {
        return base + formatter.nameSeparator() + part;
    }

    private long ns2ms(long nanoseconds) {
        return nanoseconds / 1_000_000L;
    }

    private double ns2ms(double nanoseconds) {
        return nanoseconds / 1_000_000.0;
    }
}
