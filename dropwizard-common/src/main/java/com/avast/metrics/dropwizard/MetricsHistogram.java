package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Histogram;

public class MetricsHistogram implements Histogram {

    private final String name;
    private final com.codahale.metrics.Histogram metricsHistogram;

    public MetricsHistogram(String name, com.codahale.metrics.Histogram metricsHistogram) {
        this.name = name;
        this.metricsHistogram = metricsHistogram;
    }

    @Override
    public void update(long value) {
        metricsHistogram.update(value);
    }

    @Override
    public String getName() {
        return name;
    }

}
