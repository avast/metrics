package com.avast.metrics.core;

import com.avast.metrics.api.Timer;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TimerHelper {

    public static <T> T time(Callable<T> operation,Timer successTimer, Timer failureTimer) throws Exception {
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

    public static <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation,Timer successTimer, Executor executor) throws Exception {
        Timer.TimeContext context = successTimer.start();
        try {
            CompletableFuture<T> promise = new CompletableFuture<>();
            CompletableFuture<T> future = operation.call();
            future.handleAsync((success, failure) -> {
                context.stop();
                if (failure == null) {
                    promise.complete(success);
                } else {
                    promise.completeExceptionally(failure);
                }
                return null;
            }, executor);
            return promise;
        } catch (Exception ex) {
            context.stop();
            throw ex;
        }
    }

    public static <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Timer successTimer,Timer failureTimer, Executor executor) throws Exception {
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
