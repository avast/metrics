package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Timer;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class MetricsTimer implements Timer {

    private final com.codahale.metrics.Timer metricsTimer;

    public MetricsTimer(com.codahale.metrics.Timer metricsTimer) {
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
    public <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Executor executor) {
        com.codahale.metrics.Timer.Context successContext = metricsTimer.time();
        CompletableFuture<T> result = new CompletableFuture<>();
        try {
            CompletableFuture<T> future = operation.call();
            future.handleAsync((success, failure) -> {
                if (success != null) {
                    result.complete(success);
                    successContext.stop();
                } else {
                    result.completeExceptionally(failure);
                }
                return null;
            });
        } catch (Exception ex) {
            result.completeExceptionally(ex);
        }
        return result;
    }

    public <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Timer failureTimer, Executor executor) {
        com.codahale.metrics.Timer.Context successContext = metricsTimer.time();
        TimeContext failureContext = failureTimer.start();
        CompletableFuture<T> result = new CompletableFuture<>();
        try {
            CompletableFuture<T> future = operation.call();
            future.handleAsync((success, failure) -> {
                if (success != null) {
                    result.complete(success);
                    successContext.stop();
                } else {
                    result.completeExceptionally(failure);
                    failureContext.stop();
                }
                return null;
            });
        } catch (Exception ex) {
            failureContext.stop();
            result.completeExceptionally(ex);
        }
        return result;
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
