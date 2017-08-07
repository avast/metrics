package com.avast.metrics.dropwizard.formatting.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

/**
 * Configuration for fields formatting.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FormatterConfig {
    static final String SECTION_DEFAULTS = "metricsFormattingDefaults";

    public static FormatterConfig fromConfig(Config config) {
        Config referenceConfig = ConfigFactory.defaultReference().getConfig(SECTION_DEFAULTS);
        Config mergedConfig = config.withFallback(referenceConfig);
        return ConfigBeanFactory.create(mergedConfig, FormatterConfig.class);
    }

    private CounterConfig counter;
    private GaugeConfig gauge;
    private MeterConfig meter;
    private HistogramConfig histogram;
    private TimerConfig timer;

    public CounterConfig getCounter() {
        return counter;
    }

    public void setCounter(CounterConfig counter) {
        this.counter = counter;
    }

    public GaugeConfig getGauge() {
        return gauge;
    }

    public void setGauge(GaugeConfig gauge) {
        this.gauge = gauge;
    }

    public MeterConfig getMeter() {
        return meter;
    }

    public void setMeter(MeterConfig meter) {
        this.meter = meter;
    }

    public HistogramConfig getHistogram() {
        return histogram;
    }

    public void setHistogram(HistogramConfig histogram) {
        this.histogram = histogram;
    }

    public TimerConfig getTimer() {
        return timer;
    }

    public void setTimer(TimerConfig timer) {
        this.timer = timer;
    }
}

