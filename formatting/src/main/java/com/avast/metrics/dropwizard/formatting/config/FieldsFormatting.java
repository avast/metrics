package com.avast.metrics.dropwizard.formatting.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

/**
 * Configuration for fields formatting.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FieldsFormatting {
    static final String SECTION_DEFAULTS = "metricsFieldsFormattingDefaults";

    public static FieldsFormatting fromConfig(Config config) {
        Config referenceConfig = ConfigFactory.defaultReference().getConfig(SECTION_DEFAULTS);
        Config mergedConfig = config.withFallback(referenceConfig);
        return ConfigBeanFactory.create(mergedConfig, FieldsFormatting.class);
    }

    private CounterFormatting counter;
    private GaugeFormatting gauge;
    private MeterFormatting meter;
    private HistogramFormatting histogram;
    private TimerFormatting timer;

    public CounterFormatting getCounter() {
        return counter;
    }

    public void setCounter(CounterFormatting counter) {
        this.counter = counter;
    }

    public GaugeFormatting getGauge() {
        return gauge;
    }

    public void setGauge(GaugeFormatting gauge) {
        this.gauge = gauge;
    }

    public MeterFormatting getMeter() {
        return meter;
    }

    public void setMeter(MeterFormatting meter) {
        this.meter = meter;
    }

    public HistogramFormatting getHistogram() {
        return histogram;
    }

    public void setHistogram(HistogramFormatting histogram) {
        this.histogram = histogram;
    }

    public TimerFormatting getTimer() {
        return timer;
    }

    public void setTimer(TimerFormatting timer) {
        this.timer = timer;
    }
}

