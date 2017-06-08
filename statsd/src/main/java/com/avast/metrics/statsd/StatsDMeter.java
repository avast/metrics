package com.avast.metrics.statsd;

import com.avast.metrics.api.Meter;
import com.timgroup.statsd.StatsDClient;

import java.util.concurrent.atomic.AtomicInteger;

public class StatsDMeter implements Meter {
    private final StatsDClient client;
    private final String name;

    private final AtomicInteger marks = new AtomicInteger(0);

    public StatsDMeter(final StatsDClient client, final String name) {
        this.client = client;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void mark() {
        marks.incrementAndGet();
        client.count(name, 1);
    }

    @Override
    public void mark(final long n) {
        marks.incrementAndGet();
        client.count(name, n);
    }

    @Override
    public long count() {
        return marks.get();
    }
}
