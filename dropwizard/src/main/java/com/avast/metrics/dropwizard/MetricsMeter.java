package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Meter;

public class MetricsMeter implements Meter {

    private final String name;
    private final com.codahale.metrics.Meter metricsMeter;

    public MetricsMeter(String name, com.codahale.metrics.Meter metricsMeter) {
        this.name = name;
        this.metricsMeter = metricsMeter;
    }

    @Override
    public void mark() {
        metricsMeter.mark();
    }

    @Override
    public void mark(long n) {
        metricsMeter.mark(n);
    }

    @Override
    public long count() {
        return metricsMeter.getCount();
    }

    @Override
    public String getName() {
        return name;
    }

}
