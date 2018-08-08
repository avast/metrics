package com.avast.metrics.api;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface TimerPair {

    interface TimeContext extends AutoCloseable {
        void stop();

        void stopFailure();

        @Override
        default void close() {
            stop();
        }
    }

    TimeContext start();

    void update(Duration duration);

    void updateFailure(Duration duration);

    <T> T time(Callable<T> operation) throws Exception;

    <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Executor executor) throws Exception;

}
