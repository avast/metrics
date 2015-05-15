package com.avast.metrics.api;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface Timer {

    interface TimeContext {

        void stop();
    }

    TimeContext start();

    void update(Duration duration);

    <T> T time(Callable<T> operation) throws Exception;

    <T> T time(Callable<T> operation, Timer failureTimer) throws Exception;

    <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Executor executor);

    <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Timer failureTimer, Executor executor);

}
