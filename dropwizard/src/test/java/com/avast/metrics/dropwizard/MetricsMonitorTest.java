package com.avast.metrics.dropwizard;

import com.avast.metrics.api.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

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

}
