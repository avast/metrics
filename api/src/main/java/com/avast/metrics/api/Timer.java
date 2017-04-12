package com.avast.metrics.api;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface Timer extends Counting {

    interface TimeContext extends AutoCloseable {

        void stop();

        /**
         * Stops the timer and returns elapsed time in nanoseconds.
         */
        default long stopAndGetTime() {
            throw new UnsupportedOperationException();
        }

        /**
         * Close the resource, {@link #stop()} the time context by default.
         */
        default void close() {
            stop();
        }
    }

    TimeContext start();

    void update(Duration duration);

    /**
     * Measure and record duration of operation. Both success and failure must take effect and update the state.
     * {@code operation} is always evaluated just once.
     *
     * @param operation operation to execute and measure its duration
     * @param <T>       the type of value returned by {@code operation}
     * @return the value returned by {@code operation}
     * @throws Exception if {@code operation} throws an {@link Exception}
     */
    <T> T time(Callable<T> operation) throws Exception;

    /**
     * Measure and record duration of operation. In case of success update state of this timer, in case of failure
     * update state of {@code failureTimer}. {@code operation} is always evaluated just once.
     *
     * @param operation    operation to execute and measure its duration
     * @param failureTimer timer to update in case of failure (exception)
     * @param <T>          the type of value returned by {@code operation}
     * @return the value returned by {@code operation}
     * @throws Exception if {@code operation} throws an {@link Exception}
     */
    <T> T time(Callable<T> operation, Timer failureTimer) throws Exception;

    /**
     * Measure and record duration of operation executed asynchronously. Both success and failure must take effect
     * and update the state. {@code operation} is always evaluated just once.
     *
     * @param operation operation to execute and measure its duration
     * @param executor  executor where the operation will be run
     * @param <T>       the type of value returned by {@code operation}
     * @return the value returned by {@code operation}
     * @throws Exception if {@code operation} throws an {@link Exception}
     */
    <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Executor executor) throws Exception;

    /**
     * Measure and record duration of operation executed asynchronously. In case of success update state of this timer,
     * in case of failure update state of {@code failureTimer}. {@code operation} is always evaluated just once.
     *
     * @param operation    operation to execute and measure its duration
     * @param failureTimer timer to update in case of failure (exception)
     * @param executor     executor where the operation will be run
     * @param <T>          the type of value returned by {@code operation}
     * @return the value returned by {@code operation}
     * @throws Exception if {@code operation} throws an {@link Exception}
     */
    <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Timer failureTimer, Executor executor) throws Exception;

}
