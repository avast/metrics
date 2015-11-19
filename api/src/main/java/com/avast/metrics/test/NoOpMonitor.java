package com.avast.metrics.test;

import com.avast.metrics.api.Counter;
import com.avast.metrics.api.Gauge;
import com.avast.metrics.api.Histogram;
import com.avast.metrics.api.Meter;
import com.avast.metrics.api.Metric;
import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Timer;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class NoOpMonitor implements Monitor {

    private NoOpMonitor() {
    }

    public static final Monitor INSTANCE = new NoOpMonitor();

    @Override
    public Monitor named(String name) {
        return this;
    }

    @Override
    public Monitor named(String name1, String name2, String... restOfNames) { return this; }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Meter newMeter(String String) {
        return new Meter() {
            @Override
            public void mark() {
            }

            @Override
            public void mark(long n) {
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public String getName() {
                return "";
            }
        };
    }

    @Override
    public Counter newCounter(String String) {
        return new Counter() {
            @Override
            public void inc() {
            }

            @Override
            public void inc(long n) {
            }

            @Override
            public void dec() {
            }

            @Override
            public void dec(int n) {
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public String getName() {
                return "";
            }
        };
    }

    @Override
    public Timer newTimer(String String) {
        return new Timer() {
            @Override
            public TimeContext start() {
                return () -> {
                };
            }

            @Override
            public void update(Duration duration) {
            }

            @Override
            public <T> T time(Callable<T> operation) throws Exception {
                return operation.call();
            }

            @Override
            public <T> T time(Callable<T> operation, Timer failureTimer) throws Exception {
                return operation.call();
            }

            @Override
            public <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Executor executor) throws Exception {
                return operation.call();
            }

            @Override
            public <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Timer failureTimer, Executor executor) throws Exception {
                return operation.call();
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public String getName() {
                return "";
            }
        };
    }

    @Override
    public <T> Gauge<T> newGauge(String String, Supplier<T> gauge) {
        return new Gauge<T>() {
            @Override
            public T getValue() {
                return gauge.get();
            }

            @Override
            public String getName() {
                return "";
            }
        };
    }

    @Override
    public Histogram newHistogram(String String) {
        return new Histogram() {
            @Override
            public void update(long value) {
            }

            @Override
            public String getName() {
                return "";
            }
        };
    }

    @Override
    public void remove(Metric metric) {
    }

    @Override
    public void close() {
    }
}
