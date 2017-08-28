package com.avast.metrics.statsd;

import com.avast.metrics.api.Counter;
import com.timgroup.statsd.StatsDClient;

import java.util.concurrent.atomic.AtomicLong;

public class StatsDCounter implements Counter {
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
        client.count(name, 1, sampleRate);
    }

    @Override
    public void inc(final long n) {
        count.addAndGet(n);
        client.count(name, n, sampleRate);
    }

    @Override
    public void dec() {
        count.decrementAndGet();
        client.count(name, -1, sampleRate);
    }

    @Override
    public void dec(final int n) {
        final int delta = -n;

        count.addAndGet(delta);
        client.count(name, delta, sampleRate);
    }

    @Override
    public long count() {
        return count.get();
    }
}
