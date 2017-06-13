package com.avast.metrics.statsd;

import com.timgroup.statsd.StatsDClient;
import org.junit.Test;
import org.mockito.Matchers;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class StatsDTimerTest {

    @Test
    public void testCounts() throws Exception {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDTimer timer = new StatsDTimer(client, name);
        final StatsDTimer failureTimer = new StatsDTimer(client, name);

        for (int i = 1; i <= 5; i++) {
            timer.time(() -> "");
        }

        // test failure:
        try {
            timer.time(() -> {
                throw new RuntimeException();
            }, failureTimer);
            fail("exception should have been thrown");
        } catch (Exception e) {
            // ok
        }

        timer.update(Duration.ZERO);

        // it's important to wait for the future
        timer.timeAsync(() -> CompletableFuture.completedFuture(""), ForkJoinPool.commonPool()).get();
        timer.timeAsync(() -> CompletableFuture.completedFuture(""), failureTimer, ForkJoinPool.commonPool()).get();

        // test failures:

        final CompletableFuture<String> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException());

        try {
            timer.timeAsync(() -> failedFuture, ForkJoinPool.commonPool()).get();
            fail("exception should have been thrown");
        } catch (Exception e) {
            // ok
        }

        try {
            timer.timeAsync(() -> failedFuture, failureTimer, ForkJoinPool.commonPool()).get();
            fail("exception should have been thrown");
        } catch (Exception e) {
            // ok
        }

        // verify results

        verify(client, times(11)).recordExecutionTime(Matchers.eq(name), Matchers.anyLong(), Matchers.anyVararg());

        assertEquals(9, timer.count());
        assertEquals(2, failureTimer.count());
    }

    @Test
    public void testTimerContext() {
        final TestClock clock = new TestClock(0, 100);

        final AtomicReference<Duration> r = new AtomicReference<>(null);

        // the ctor wants supplier for nanos, but it doesn't matter in test so we pass it as millis and then test is as millis
        final StatsDTimer.StatsDTimerContext context = new StatsDTimer.StatsDTimerContext(() -> clock.instant().toEpochMilli(), r::set);

        assertEquals(100 , context.stopAndGetTime());
        assertEquals(100 , context.stopAndGetTime()); // repeated by purpose!

        assertEquals(100, r.get().toNanos());
    }
}
