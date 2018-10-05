package com.avast.metrics.statsd;

import com.avast.metrics.api.Counter;
import com.timgroup.statsd.StatsDClient;

import java.util.concurrent.atomic.AtomicLong;

public class StatsDCounter implements Counter, StatsDMetric {
    private final StatsDClient client;
    private final String name;
    private double sampleRate;

    private final AtomicLong count = new AtomicLong(0);

    StatsDCounter(final StatsDClient client, final String name) {
        this(client, name, 1.0);
    }

    StatsDCounter(final StatsDClient client, final String name, double sampleRate) {
        this.client = client;
        this.name = name;
        this.sampleRate = sampleRate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void inc() {
        count.incrementAndGet();
        underlying(1);
    }

    @Override
    public void inc(final long n) {
        count.addAndGet(n);
        underlying(n);
    }

    @Override
    public void dec() {
        count.decrementAndGet();
        underlying(-1);
    }

    @Override
    public void dec(final int n) {
        final int delta = -n;

        count.addAndGet(delta);
        underlying(delta);
    }

    @Override
    public long count() {
        return count.get();
    }


    @Override
    public void init() {
        client.count(name, 0, sampleRate);
    }

    private void underlying(final long value) {
        client.count(name, value, sampleRate);
    }
}
