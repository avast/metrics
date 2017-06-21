package com.avast.metrics.statsd;

import com.timgroup.statsd.StatsDClient;
import org.junit.Test;
import org.mockito.Matchers;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StatsDMetricsMonitorTest {
    @Test
    public void testGeneratesCorrectNames() {
        final String name = TestUtils.randomString();

        final StatsDMetricsMonitor monitor = new StatsDMetricsMonitor("", 1234, "com.avast.domain").named("named");

        assertEquals("named." + name, monitor.constructMetricName(name));
    }

    @Test
    public void testSchedulesGaugeSending() {
        final StatsDClient statsDClient = mock(StatsDClient.class);
        final ScheduledExecutorService scheduler = mock(ScheduledExecutorService.class);

        doAnswer(invocation -> {
            final Runnable runnable = invocation.getArgumentAt(0, Runnable.class);

            for (int i = 1; i <= 5; i++) {
                runnable.run();
            }

            return mock(ScheduledFuture.class);
        }).when(scheduler).scheduleAtFixedRate(Matchers.any(), Matchers.anyLong(), Matchers.anyLong(), Matchers.any());

        final StatsDMetricsMonitor monitor = new StatsDMetricsMonitor("", 1234, "com.avast.domain", Duration.ZERO, scheduler) {
            @Override
            protected StatsDClient createStatsDClient(final String host, final int port, final String domain) {
                return statsDClient;
            }
        };

        final String name = TestUtils.randomString();

        assertNotNull(monitor.newGauge(name, () -> Math.PI));

        verify(scheduler, times(1)).scheduleAtFixedRate(Matchers.any(), Matchers.anyLong(), Matchers.anyLong(), Matchers.any());

        verify(statsDClient, times(5)).recordGaugeValue(Matchers.eq(name), Matchers.eq(Math.PI));
    }


    @Test
    public void testCancelsGaugeSendingAfterReplacing() {
        final StatsDClient statsDClient = mock(StatsDClient.class);
        final ScheduledExecutorService scheduler = mock(ScheduledExecutorService.class);
        final ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);

        doAnswer(invocation -> {
            final Runnable runnable = invocation.getArgumentAt(0, Runnable.class);

            runnable.run();

            return scheduledFuture;
        }).when(scheduler).scheduleAtFixedRate(Matchers.any(), Matchers.anyLong(), Matchers.anyLong(), Matchers.any());

        final StatsDMetricsMonitor monitor = new StatsDMetricsMonitor("", 1234, "com.avast.domain", Duration.ZERO, scheduler) {
            @Override
            protected StatsDClient createStatsDClient(final String host, final int port, final String domain) {
                return statsDClient;
            }
        };

        final String name = TestUtils.randomString();

        // request gauge
        assertNotNull(monitor.newGauge(name, () -> Math.PI));

        verify(scheduler, times(1)).scheduleAtFixedRate(Matchers.any(), Matchers.anyLong(), Matchers.anyLong(), Matchers.any());
        verify(statsDClient, times(1)).recordGaugeValue(Matchers.eq(name), Matchers.eq(Math.PI));


        // request the same again, the old one should be cancelled
        assertNotNull(monitor.newGauge(name, true, () -> Math.PI));

        verify(statsDClient, times(2)).recordGaugeValue(Matchers.eq(name), Matchers.eq(Math.PI));
        verify(scheduler, times(2)).scheduleAtFixedRate(Matchers.any(), Matchers.anyLong(), Matchers.anyLong(), Matchers.any());
        verify(scheduledFuture, times(1)).cancel(Matchers.anyBoolean());
    }

    @Test
    public void failsForGaugeIfNotReplacing() {
        final StatsDMetricsMonitor monitor = new StatsDMetricsMonitor("", 1234, "com.avast.domain");

        final String name = TestUtils.randomString();

        assertNotNull(monitor.newGauge(name, () -> Math.PI));

        monitor.newGauge(name, true, () -> Math.PI); // ok

        try {
            monitor.newGauge(name, false, () -> Math.PI);
            fail("Exception should have been thrown");
        } catch (IllegalStateException e) {
            // ok
        }
    }
}
