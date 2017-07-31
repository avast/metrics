package com.avast.metrics.dropwizard.formatting;

import com.avast.metrics.api.*;
import com.avast.metrics.dropwizard.MetricsMonitor;
import com.codahale.metrics.MetricRegistry;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
}
