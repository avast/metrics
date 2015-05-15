package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Histogram;

public class MetricsHistogram implements Histogram {
    private final com.codahale.metrics.Histogram metricsHistogram;

    public MetricsHistogram(com.codahale.metrics.Histogram metricsHistogram) {
        this.metricsHistogram = metricsHistogram;
    }

    @Override
    public void update(long value) {
        metricsHistogram.update(value);
    }
}
