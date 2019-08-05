package com.avast.metrics.grpc;

import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Timer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class MetricsCache {
    private final Monitor monitor;
    private final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> gaugedValues = new ConcurrentHashMap<>();

    public MetricsCache(final Monitor monitor) {
        this.monitor = monitor;
    }

    public Timer getTimer(String name) {
        return timers.computeIfAbsent(name, monitor::newTimer);
    }

    public AtomicInteger getGaugedValue(String name) { return gaugedValues.computeIfAbsent(name, n -> {
        AtomicInteger v = new AtomicInteger();
        monitor.newGauge(n, v::get);
        return v;
    }); }
}
