package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Gauge;
import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Timer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MetricsMonitorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void jmxClose() {
        try (Monitor m1 = new JmxMetricsMonitor("com.avast.metrics.test").named("test-jmx-close")) {
            int value1 = 1;
            Gauge<Integer> gauge1 = m1.newGauge("gauge", () -> value1);
            assertEquals(value1, (int) gauge1.getValue());
        }

        try (Monitor m2 = new JmxMetricsMonitor("com.avast.metrics.test").named("test-jmx-close")) {
            int value2 = 2;
            Gauge<Integer> gauge2 = m2.newGauge("gauge", () -> value2);
            assertEquals(value2, (int) gauge2.getValue());
        }

        // watch log output for javax.management.InstanceAlreadyExistsException
    }

    @Test
    public void getName() {
        try (Monitor m1 = new JmxMetricsMonitor("com.avast.metrics.test").named("first", "second", "third", "fourth")) {
            final String name = m1.getName();

            assertEquals("first/second/third/fourth", name);
        }

        try (Monitor m1 = new JmxMetricsMonitor("com.avast.metrics.test").named("first/sub").named("second").named("third").named("fourth/fifth")) {
            try (Monitor m2 = new JmxMetricsMonitor("com.avast.metrics.test").named("first/sub", "second", "third").named("fourth/fifth")) {
                final String name1 = m1.getName();
                final String name2 = m2.getName();

                assertEquals(name1, name2);
            }
        }
    }

    @Test
    public void timing() throws InterruptedException {
        long toleranceNanos = 10000000;
        try (Monitor monitor = new MetricsMonitor().named("timing")) {
            Timer timer = monitor.newTimer("test");
            Timer.TimeContext ctx = timer.start();
            long time1 = System.nanoTime();
            long elapsedTime = ctx.stopAndGetTime();
            long time2 = System.nanoTime();
            long measuredElapsedTime = time2 - time1;
            assertTrue(Math.abs(measuredElapsedTime - elapsedTime) < toleranceNanos);
        }
    }

}
