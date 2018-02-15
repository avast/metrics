package com.avast.metrics.grpc;

import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Timer;

import java.util.concurrent.ConcurrentHashMap;

class TimersCache {
    private final Monitor monitor;
    private final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<>();

    public TimersCache(final Monitor monitor) {
        this.monitor = monitor;
    }

    public Timer get(String name) {
        if (timers.containsKey(name)) {
            return timers.get(name);
        } else {
            synchronized (timers) {
                return timers.computeIfAbsent(name, monitor::newTimer);
            }
        }
    }
}
