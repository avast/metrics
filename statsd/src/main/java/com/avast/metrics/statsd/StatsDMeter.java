package com.avast.metrics.statsd;

import com.avast.metrics.api.Meter;
import com.timgroup.statsd.StatsDClient;

import java.util.concurrent.atomic.AtomicLong;

public class StatsDMeter implements Meter {
    private final StatsDClient client;
    private final String name;
    private double sampleRate;

    private final AtomicLong marks = new AtomicLong(0);

    StatsDMeter(final StatsDClient client, final String name) {
        this(client, name, 1.0);
    }

    StatsDMeter(final StatsDClient client, final String name, double sampleRate) {
        this.client = client;
        this.name = name;
        this.sampleRate = sampleRate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void mark() {
        marks.incrementAndGet();
        underlying(1);
    }

    @Override
    public void mark(final long n) {
        marks.addAndGet(n);
        underlying(n);
    }

    @Override
    public long count() {
        return marks.get();
    }

    private void underlying(long value) {
        client.count(name, value, sampleRate);
    }
}
