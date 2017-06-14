package com.avast.metrics.statsd;

import com.avast.metrics.api.Timer;
import com.avast.metrics.core.TimerHelper;
import com.timgroup.statsd.StatsDClient;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
public class StatsDTimer implements Timer {
    private final StatsDClient client;
    private final String name;

    private final AtomicLong count = new AtomicLong(0);

    StatsDTimer(final StatsDClient client, final String name) {
        this.client = client;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TimeContext start() {
        return new StatsDTimerContext(this::update);
    }

    @Override
    public void update(final Duration duration) {
        count.incrementAndGet();
        client.recordExecutionTime(name, duration.toMillis());
    }

    @Override
    public <T> T time(final Callable<T> operation) throws Exception {
        final TimeContext context = start();
        final T result = operation.call();
        context.stop();
        return result;
    }

    @Override
    public <T> T time(Callable<T> operation, Timer failureTimer) throws Exception {
        return TimerHelper.time(operation, this, failureTimer);
    }

    @Override
    public <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Executor executor) throws Exception {
        return TimerHelper.timeAsync(operation, this, executor);
    }

    @Override
    public <T> CompletableFuture<T> timeAsync(Callable<CompletableFuture<T>> operation, Timer failureTimer, Executor executor) throws Exception {
        return TimerHelper.timeAsync(operation, this, executor);
    }

    @Override
    public long count() {
        return count.get();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static class StatsDTimerContext implements TimeContext {

        private final Supplier<Long> clock;
        private final long start;
        private Optional<Duration> length = Optional.empty();
        private final Consumer<Duration> update;

        private final Object lock = new Object();

        StatsDTimerContext(final Supplier<Long> clock, final Consumer<Duration> updateFunction) {
            this.clock = clock;
            this.start = clock.get();
            this.update = updateFunction;
        }

        StatsDTimerContext(final Consumer<Duration> updateFunction) {
            this(System::nanoTime, updateFunction);
        }

        @Override
        public void stop() {
            stopAndGetTime();
        }

        @Override
        public long stopAndGetTime() {
            synchronized (lock) {
                return length.orElseGet(() -> {
                    final Duration d = Duration.ofNanos(clock.get() - start);
                    update.accept(d);
                    length = Optional.of(d);
                    return d;
                }).toNanos();
            }
        }

        @Override
        public void close() {
            // noop
        }
    }
}
