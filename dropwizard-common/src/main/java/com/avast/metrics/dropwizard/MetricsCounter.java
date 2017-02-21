package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Counter;

public class MetricsCounter implements Counter {

    private final String name;
    private final com.codahale.metrics.Counter metricsCounter;

    public MetricsCounter(String name, com.codahale.metrics.Counter metricsCounter) {
        this.name = name;
        this.metricsCounter = metricsCounter;
    }

    @Override
    public void inc() {
        metricsCounter.inc();
    }

    @Override
    public void inc(long n) {
        metricsCounter.inc(n);
    }

    @Override
    public void dec() {
        metricsCounter.dec();
    }

    @Override
    public void dec(int n) {
        metricsCounter.dec(n);
    }

    @Override
    public long count() {
        return metricsCounter.getCount();
    }

    @Override
    public String getName() {
        return name;
    }

}
