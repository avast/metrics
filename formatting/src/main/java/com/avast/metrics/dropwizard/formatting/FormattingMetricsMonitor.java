package com.avast.metrics.dropwizard.formatting;

import com.avast.metrics.api.*;
import com.avast.metrics.api.Timer;
import com.avast.metrics.dropwizard.MetricsMonitor;
import com.avast.metrics.dropwizard.formatting.config.FieldsFormatting;
import com.avast.metrics.dropwizard.formatting.config.HistogramFormatting;
import com.avast.metrics.dropwizard.formatting.config.MeterFormatting;
import com.avast.metrics.dropwizard.formatting.config.TimerFormatting;
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
public class FormattingMetricsMonitor extends MetricsMonitor {
    private final Formatter formatter;

    public FormattingMetricsMonitor(Formatter formatter) {
        this(new MetricRegistry(), Naming.defaultNaming(), formatter);
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
     */
    @Override
    public String getName() {
        return constructMetricName(Optional.empty(), separator());
    }

    public String format(MetricsFilter filter, FieldsFormatting fieldsFormatting) {
        Stream<MetricValues> metrics = registry.getMetrics()
                .entrySet()
                .stream()
                .filter(entry -> filter.isEnabled(entry.getKey()))
                .map(entry -> toMetricValue(entry.getKey(), entry.getValue(), fieldsFormatting))
                .filter(values -> !values.getFieldsValues().isEmpty())
                .sorted(Comparator.comparing(MetricValues::getName));

        return formatter.format(metrics);
    }

    private MetricValues toMetricValue(String name, com.codahale.metrics.Metric metric, FieldsFormatting fieldsFormatting) {
        if (metric instanceof com.codahale.metrics.Counter) {
            return mapCounter(name, (com.codahale.metrics.Counter) metric, fieldsFormatting);
        } else if (metric instanceof com.codahale.metrics.Gauge) {
            return mapGauge(name, (com.codahale.metrics.Gauge) metric, fieldsFormatting);
        } else if (metric instanceof com.codahale.metrics.Meter) {
            return mapMeter(name, (com.codahale.metrics.Meter) metric, fieldsFormatting);
        } else if (metric instanceof com.codahale.metrics.Histogram) {
            return mapHistogram(name, (com.codahale.metrics.Histogram) metric, fieldsFormatting);
        } else if (metric instanceof com.codahale.metrics.Timer) {
            return mapTimer(name, (com.codahale.metrics.Timer) metric, fieldsFormatting);
        } else {
            LOGGER.error("Unexpected metric class: {}", metric.getClass());
            return new MetricValues(name, Collections.emptyMap());
        }
    }

    private MetricValues mapCounter(String name, com.codahale.metrics.Counter counter, FieldsFormatting fieldsFormatting) {
        if (fieldsFormatting.getCounter().isCount()) {
            return new MetricValues(name, Collections.singletonMap("count", formatter.formatNumber(counter.getCount())));
        } else {
            return new MetricValues(name, Collections.emptyMap());
        }
    }

    private MetricValues mapGauge(String name, com.codahale.metrics.Gauge gauge, FieldsFormatting fieldsFormatting) {
        if (fieldsFormatting.getGauge().isValue()) {
            return new MetricValues(name, Collections.singletonMap("value", formatter.formatObject(gauge.getValue())));
        } else {
            return new MetricValues(name, Collections.emptyMap());
        }
    }

    private MetricValues mapMeter(String name, com.codahale.metrics.Meter meter, FieldsFormatting fieldsFormatting) {
        MeterFormatting meterFormatting = fieldsFormatting.getMeter();
        Map<String, String> fieldsValues = new HashMap<>();

        if (meterFormatting.isCount())
            fieldsValues.put("count", formatter.formatNumber(meter.getCount()));
        if (meterFormatting.isMean())
            fieldsValues.put("mean", formatter.formatNumber(meter.getMeanRate()));
        if (meterFormatting.isOneMinuteRate())
            fieldsValues.put("m1", formatter.formatNumber(meter.getOneMinuteRate()));
        if (meterFormatting.isFiveMinuteRate())
            fieldsValues.put("m5", formatter.formatNumber(meter.getFiveMinuteRate()));
        if (meterFormatting.isFifteenMinuteRate())
            fieldsValues.put("m15", formatter.formatNumber(meter.getFifteenMinuteRate()));

        return new MetricValues(name, fieldsValues);
    }

    private MetricValues mapHistogram(String name, com.codahale.metrics.Histogram histogram, FieldsFormatting fieldsFormatting) {
        HistogramFormatting histogramFormatting = fieldsFormatting.getHistogram();
        Snapshot snapshot = histogram.getSnapshot();
        Map<String, String> fieldsValues = new HashMap<>();

        if (histogramFormatting.isCount())
            fieldsValues.put("count", formatter.formatNumber(histogram.getCount()));
        if (histogramFormatting.isMin())
            fieldsValues.put("min", formatter.formatNumber(snapshot.getMin()));
        if (histogramFormatting.isMax())
            fieldsValues.put("max", formatter.formatNumber(snapshot.getMax()));
        if (histogramFormatting.isMean())
            fieldsValues.put("mean", formatter.formatNumber(snapshot.getMean()));
        if (histogramFormatting.isStdDev())
            fieldsValues.put("stddev", formatter.formatNumber(snapshot.getStdDev()));

        histogramFormatting
                .getPercentiles()
                .forEach(percentile -> fieldsValues.put(percentileName(percentile), formatter.formatNumber(snapshot.getValue(percentile))));

        return new MetricValues(name, fieldsValues);
    }

    private MetricValues mapTimer(String name, com.codahale.metrics.Timer timer, FieldsFormatting fieldsFormatting) {
        TimerFormatting timerFormatting = fieldsFormatting.getTimer();
        Snapshot snapshot = timer.getSnapshot();
        Map<String, String> fieldsValues = new HashMap<>();

        if (timerFormatting.isCount())
            fieldsValues.put("count", formatter.formatNumber(timer.getCount()));
        if (timerFormatting.isMean())
            fieldsValues.put("mean", formatter.formatNumber(timer.getMeanRate()));
        if (timerFormatting.isOneMinuteRate())
            fieldsValues.put("m1", formatter.formatNumber(timer.getOneMinuteRate()));
        if (timerFormatting.isFiveMinuteRate())
            fieldsValues.put("m5", formatter.formatNumber(timer.getFiveMinuteRate()));
        if (timerFormatting.isFifteenMinuteRate())
            fieldsValues.put("m15", formatter.formatNumber(timer.getFifteenMinuteRate()));
        if (timerFormatting.isMin())
            fieldsValues.put("min", formatter.formatNumber(snapshot.getMin()));
        if (timerFormatting.isMax())
            fieldsValues.put("max", formatter.formatNumber(snapshot.getMax()));
        if (timerFormatting.isMean())
            fieldsValues.put("mean", formatter.formatNumber(snapshot.getMean()));
        if (timerFormatting.isStdDev())
            fieldsValues.put("stddev", formatter.formatNumber(snapshot.getStdDev()));

        timerFormatting
                .getPercentiles()
                .forEach(percentile -> fieldsValues.put(percentileName(percentile), formatter.formatNumber(snapshot.getValue(percentile))));

        return new MetricValues(name, fieldsValues);
    }

    private String percentileName(Double percentile) {
        return formatter.sanitizeName(String.format(Locale.ENGLISH, "p%s", percentile * 100));
    }
}
