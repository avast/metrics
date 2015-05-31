package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Counter;
import com.avast.metrics.api.Meter;
import com.avast.metrics.api.Timer;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MetricsMonitorTest {

    @Test
    public void basic() {
        MetricsMonitor monitor = new MetricsMonitor(true);

        Meter meter = monitor.newMeter("meter");
        meter.mark();
        assertTrue(meter.count() == 1);

        Counter counter = monitor.newCounter("counter");
        counter.inc();
        counter.inc();
        counter.inc();
        assertTrue(counter.count() == 3);

        Timer timer = monitor.newTimer("timer");
        timer.update(Duration.ofSeconds(20));
        assertTrue(timer.count() == 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void monitorDoesNotAllowDuplicateNames() {
        MetricsMonitor monitor = new MetricsMonitor(false);

        assertNotNull(monitor.newMeter("test"));
        monitor.newMeter("test");
    }

    @Test
    public void monitorGeneratesUniqueNames() {
        MetricsMonitor monitor = new MetricsMonitor(true);

        Meter m1 = monitor.newMeter("test");
        assertNotNull(m1);
        assertEquals(m1.getName(), "test");

        Meter m2 = monitor.newMeter("test");
        assertNotNull(m2);
        assertTrue(m2.getName().length() > "test".length());
        System.out.println(m2.getName());
    }

}