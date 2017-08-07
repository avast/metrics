package com.avast.metrics.dropwizard.formatting;

import com.avast.metrics.api.Counter;
import com.avast.metrics.api.TimerPair;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class FormattingMetricsMonitorTest {
    @Test
    public void testName() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("", monitor.getName());
        }
    }

    @Test
    public void testName1() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("a", monitor.named("a").getName());
        }
    }

    @Test
    public void testName2() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("a.b", monitor.named("a", "b").getName());
        }
    }

    @Test
    public void testName3() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("a.b.c", monitor.named("a", "b", "c").getName());
        }
    }

    @Test
    public void testNameIllegalCharacters() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("pi-3-14", monitor.named("pi/3.14").getName());
        }
    }

    @Test
    public void testMeterName() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("monitor.meter-x", monitor.named("monitor").newMeter("meter.x").getName());
        }
    }

    @Test
    public void testCounterName() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("monitor.counter-x", monitor.named("monitor").newCounter("counter.x").getName());
        }
    }

    @Test
    public void testTimerName() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("monitor.timer-x", monitor.named("monitor").newTimer("timer.x").getName());
        }
    }

    @Test
    public void testTimerPairName() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            TimerPair timerPair = monitor.named("monitor").newTimerPair("timer.pair.x");
            assertEquals("monitor.timer-pair-xSuccesses", timerPair.getSuccessTimer().getName());
            assertEquals("monitor.timer-pair-xFailures", timerPair.getFailureTimer().getName());
        }
    }

    @Test
    public void testGaugeName() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("monitor.gauge-x", monitor.named("monitor").newGauge("gauge.x", () -> 42).getName());
        }
    }

    @Test
    public void testGaugeName2() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("monitor.gauge-x", monitor.named("monitor").newGauge("gauge.x", true, () -> 42).getName());
        }
    }

    @Test
    public void testHistogramName() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            assertEquals("monitor.histogram-x", monitor.named("monitor").newHistogram("histogram.x").getName());
        }
    }

    @Test
    public void testFormatTwoCounters() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            Counter counterB = monitor.named("b").newCounter("counter");
            Counter counterA = monitor.named("a").newCounter("counter");
            counterB.inc(42);
            counterA.inc();

            String expected = "a.counter.count 1\n" +
                    "b.counter.count 42";

            assertEquals(expected, monitor.format());
        }
    }

    @Test
    public void testFormatGauge() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            monitor.named("gauge").newGauge("null", () -> null);
            monitor.named("gauge").newGauge("boolean", () -> true);
            monitor.named("gauge").newGauge("byte", () -> (byte) 2);
            monitor.named("gauge").newGauge("short", () -> (short) 3);
            monitor.named("gauge").newGauge("int", () -> (int) 4);
            monitor.named("gauge").newGauge("long", () -> (long) 5);
            monitor.named("gauge").newGauge("BigInteger", () -> new BigInteger("6"));
            monitor.named("gauge").newGauge("float", () -> (float) 7.0);
            monitor.named("gauge").newGauge("double", () -> (double) 7.1);
            monitor.named("gauge").newGauge("BigDecimal", () -> new BigDecimal(7.2));
            monitor.named("gauge").newGauge("String", () -> "haf");

            String expected = "gauge.BigDecimal 7.2\n" +
                    "gauge.BigInteger 6.0\n" +
                    "gauge.String haf\n" +
                    "gauge.boolean 1\n" +
                    "gauge.byte 2\n" +
                    "gauge.double 7.1\n" +
                    "gauge.float 7.0\n" +
                    "gauge.int 4\n" +
                    "gauge.long 5\n" +
                    "gauge.null null\n" +
                    "gauge.short 3";

            assertEquals(expected, monitor.format());
        }
    }
}
