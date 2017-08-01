package com.avast.metrics;

import com.avast.metrics.api.Timer;
import com.avast.metrics.api.TimerPair;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TimerPairImpl implements TimerPair {

    private Timer successTimer;
    private Timer failureTimer;

    public TimerPairImpl(Timer successTimer, Timer failureTimer) {
        this.successTimer = successTimer;
        this.failureTimer = failureTimer;
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
}
