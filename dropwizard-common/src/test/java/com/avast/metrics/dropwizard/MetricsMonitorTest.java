package com.avast.metrics.dropwizard;

import com.avast.metrics.api.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MetricsMonitorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void basic() {
        try (Monitor monitor = new MetricsMonitor().named("test-basic")) {
            Meter meter = monitor.newMeter("meter");
            meter.mark();
            assertEquals(1, meter.count());

            Counter counter = monitor.newCounter("counter");
            counter.inc();
            counter.inc();
            counter.inc();
            assertEquals(3, counter.count());

            Timer timer = monitor.newTimer("timer");
            timer.update(Duration.ofSeconds(20));
            assertEquals(1, timer.count());
        }
    }

    @Test
    public void removal() {
        try (Monitor monitor = new MetricsMonitor().named("test-removal")) {
            int value1 = 1;
            Gauge<Integer> gauge1 = monitor.newGauge("gauge", () -> value1);
            assertEquals(value1, (int) gauge1.getValue());

            monitor.remove(gauge1);

            int value2 = 2;
            Gauge<Integer> gauge2 = monitor.newGauge("gauge", () -> value2);
            assertEquals(value2, (int) gauge2.getValue());

            thrown.expect(IllegalArgumentException.class);
            int value3 = 3;
            Gauge<Integer> gauge3 = monitor.newGauge("gauge", () -> value3);
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
