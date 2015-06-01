package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Counter;
import com.avast.metrics.api.Meter;
import com.avast.metrics.api.Timer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Duration;

import static org.junit.Assert.assertTrue;

public class MetricsMonitorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void basic() {
        MetricsMonitor monitor = new MetricsMonitor();

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

}