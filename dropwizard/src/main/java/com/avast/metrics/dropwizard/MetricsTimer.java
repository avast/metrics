package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Timer;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class MetricsTimer implements Timer {

    private final String name;
    private final com.codahale.metrics.Timer metricsTimer;

    public MetricsTimer(String name, com.codahale.metrics.Timer metricsTimer) {
        this.name = name;
        this.metricsTimer = metricsTimer;
    }

    @Override
    public TimeContext start() {
        return new Context(metricsTimer.time());
    }

    @Override
    public void update(Duration duration) {
        metricsTimer.update(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> T time(Callable<T> operation) throws Exception {
        return metricsTimer.time(operation);
    }

    @Override
    public <T> T time(Callable<T> operation, Timer failureTimer) throws Exception {
        com.codahale.metrics.Timer.Context successContext = metricsTimer.time();
        TimeContext failureContext = failureTimer.start();
        try {
            T result = operation.call();
            successContext.stop();
            return result;
        } catch (Exception ex) {
            failureContext.stop();
            throw ex;
        }
    }

    @Override
    public <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Executor executor) throws Exception {
        com.codahale.metrics.Timer.Context successContext = metricsTimer.time();
        try {
            CompletableFuture<T> promise = new CompletableFuture<>();
            CompletableFuture<T> future = operation.call();
            future.handleAsync((success, failure) -> {
                successContext.stop();
                if (failure == null) {
                    promise.complete(success);
                } else {
                    promise.completeExceptionally(failure);
                }
                return null;
            }, executor);
            return promise;
        } catch (Exception ex) {
            successContext.stop();
            throw ex;
        }
    }

    public <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Timer failureTimer, Executor executor) throws Exception {
        com.codahale.metrics.Timer.Context successContext = metricsTimer.time();
        TimeContext failureContext = failureTimer.start();
        try {
            CompletableFuture<T> promise = new CompletableFuture<>();
            CompletableFuture<T> future = operation.call();
            future.handleAsync((success, failure) -> {
                if (failure == null) {
                    promise.complete(success);
                    successContext.stop();
                } else {
                    promise.completeExceptionally(failure);
                    failureContext.stop();
                }
                return null;
            }, executor);
            return promise;
        } catch (Exception ex) {
            failureContext.stop();
            throw ex;
        }
    }

    @Override
    public long count() {
        return metricsTimer.getCount();
    }

    @Override
    public String getName() {
        return name;
    }

    private static class Context implements TimeContext {

        private final com.codahale.metrics.Timer.Context ctx;

        public Context(com.codahale.metrics.Timer.Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void stop() {
            ctx.stop();
        }
    }

}
