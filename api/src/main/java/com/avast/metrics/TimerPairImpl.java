package com.avast.metrics;

import com.avast.metrics.api.Timer;
import com.avast.metrics.api.TimerPair;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TimerPairImpl implements TimerPair {

    private final Timer successTimer;
    private final Timer failureTimer;

    public TimerPairImpl(Timer successTimer, Timer failureTimer) {
        this.successTimer = successTimer;
        this.failureTimer = failureTimer;
    }

    @Override
    public TimeContext start() {
        return new Context(successTimer.start(), failureTimer.start());
    }

    @Override
    public void update(Duration duration) {
        successTimer.update(duration);
    }

    @Override
    public void updateFailure(Duration duration) {
        failureTimer.update(duration);
    }

    public <T> T time(Callable<T> operation) throws Exception {
        Timer.TimeContext successContext = successTimer.start();
        Timer.TimeContext failureContext = failureTimer.start();
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
        Timer.TimeContext successContext = successTimer.start();
        Timer.TimeContext failureContext = failureTimer.start();
        try {
            CompletableFuture<T> promise = new CompletableFuture<>();
            CompletableFuture<T> future = operation.call();
            future.handleAsync((success, failure) -> {
                if (failure == null) {
                    successContext.stop();
                    promise.complete(success);
                } else {
                    failureContext.stop();
                    promise.completeExceptionally(failure);
                }
                return null;
            }, executor);
            return promise;
        } catch (Exception ex) {
            failureContext.stop();
            throw ex;
        }
    }

    private static class Context implements TimeContext {

        private final Timer.TimeContext successContext;
        private final Timer.TimeContext failureContext;

        Context(Timer.TimeContext successContext, Timer.TimeContext failureContext) {
            this.successContext = successContext;
            this.failureContext = failureContext;
        }

        @Override
        public void stop() {
            successContext.stop();
        }

        @Override
        public void stopFailure() {
            failureContext.stop();
        }
    }
}
