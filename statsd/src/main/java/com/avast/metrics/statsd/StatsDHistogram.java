package com.avast.metrics.statsd;

import com.avast.metrics.api.Histogram;
import com.timgroup.statsd.StatsDClient;

public class StatsDHistogram implements Histogram, StatsDMetric {
    private final StatsDClient client;
    private final String name;

    public StatsDHistogram(final StatsDClient client, final String name) {
        this.client = client;
        this.name = name;
    }

    @Override
    public void update(final long value) {
        client.histogram(name, value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init() {
        this.client.recordSetValue(name, String.valueOf(0));
    }
}
