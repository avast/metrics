package com.avast.metrics.dropwizard.formatting;

import com.avast.metrics.api.Counter;
import com.avast.metrics.dropwizard.formatting.config.FieldsFormatting;
import com.avast.metrics.filter.MetricsFilter;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class FormattingMetricsMonitorTest {
    private static final FieldsFormatting FIELDS_ALL_ENABLED = FieldsFormatting.fromConfig(ConfigFactory.load().getConfig("metricsFieldsFormattingAllEnabled"));

    private FormattingMetricsMonitor newMonitor() {
        return new FormattingMetricsMonitor(new GraphiteFormatter());
    }

    @Test
    public void testName() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("", monitor.getName());
        }
    }

    @Test
    public void testName1() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("a", monitor.named("a").getName());
        }
    }

    @Test
    public void testName2() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("a.b", monitor.named("a", "b").getName());
        }
    }

    @Test
    public void testName3() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("a.b.c", monitor.named("a", "b", "c").getName());
        }
    }

    @Test
    public void testNameIllegalCharacters() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("pi-3-14", monitor.named("pi/3.14").getName());
        }
    }

    @Test
    public void testMeterName() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("monitor.meter-x", monitor.named("monitor").newMeter("meter.x").getName());
        }
    }

    @Test
    public void testCounterName() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("monitor.counter-x", monitor.named("monitor").newCounter("counter.x").getName());
        }
    }

    @Test
    public void testTimerName() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("monitor.timer-x", monitor.named("monitor").newTimer("timer.x").getName());
        }
    }

    @Test
    public void testTimerPairName() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(new GraphiteFormatter())) {
            monitor.named("monitor").newTimerPair("timer.pair.x");

            String expected = "monitor.timer-pair-xFailures.count 0\n" +
                    "monitor.timer-pair-xFailures.m1 0.0\n" +
                    "monitor.timer-pair-xFailures.m15 0.0\n" +
                    "monitor.timer-pair-xFailures.m5 0.0\n" +
                    "monitor.timer-pair-xFailures.max 0\n" +
                    "monitor.timer-pair-xFailures.mean 0.0\n" +
                    "monitor.timer-pair-xFailures.min 0\n" +
                    "monitor.timer-pair-xFailures.p50-0 0.0\n" +
                    "monitor.timer-pair-xFailures.p99-0 0.0\n" +
                    "monitor.timer-pair-xFailures.stddev 0.0\n" +
                    "monitor.timer-pair-xSuccesses.count 0\n" +
                    "monitor.timer-pair-xSuccesses.m1 0.0\n" +
                    "monitor.timer-pair-xSuccesses.m15 0.0\n" +
                    "monitor.timer-pair-xSuccesses.m5 0.0\n" +
                    "monitor.timer-pair-xSuccesses.max 0\n" +
                    "monitor.timer-pair-xSuccesses.mean 0.0\n" +
                    "monitor.timer-pair-xSuccesses.min 0\n" +
                    "monitor.timer-pair-xSuccesses.p50-0 0.0\n" +
                    "monitor.timer-pair-xSuccesses.p99-0 0.0\n" +
                    "monitor.timer-pair-xSuccesses.stddev 0.0";

            assertEquals(expected, monitor.format(MetricsFilter.ALL_ENABLED, FIELDS_ALL_ENABLED));
        }
    }

    @Test
    public void testGaugeName() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("monitor.gauge-x", monitor.named("monitor").newGauge("gauge.x", () -> 42).getName());
        }
    }

    @Test
    public void testGaugeName2() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("monitor.gauge-x", monitor.named("monitor").newGauge("gauge.x", true, () -> 42).getName());
        }
    }

    @Test
    public void testHistogramName() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            assertEquals("monitor.histogram-x", monitor.named("monitor").newHistogram("histogram.x").getName());
        }
    }

    @Test
    public void testFormatTwoCountersEnabled() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            Counter counterB = monitor.named("b").newCounter("counter");
            Counter counterA = monitor.named("a").newCounter("counter");
            counterB.inc(42);
            counterA.inc();

            String expected = "a.counter.count 1\n" +
                    "b.counter.count 42";

            assertEquals(expected, monitor.format(MetricsFilter.ALL_ENABLED, FIELDS_ALL_ENABLED));
        }
    }

    @Test
    public void testFormatTwoCountersDisabled() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
            Counter counterB = monitor.named("b").newCounter("counter");
            Counter counterA = monitor.named("a").newCounter("counter");
            counterB.inc(42);
            counterA.inc();

            assertEquals("", monitor.format(MetricsFilter.ALL_DISABLED, FIELDS_ALL_ENABLED));
        }
    }

    @SuppressWarnings("RedundantCast")
    @Test
    public void testFormatGauge() throws Exception {
        try (FormattingMetricsMonitor monitor = newMonitor()) {
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

            String expected = "gauge.BigDecimal.value 7.2\n" +
                    "gauge.BigInteger.value 6.0\n" +
                    "gauge.String.value unsupported\n" +
                    "gauge.boolean.value 1\n" +
                    "gauge.byte.value 2\n" +
                    "gauge.double.value 7.1\n" +
                    "gauge.float.value 7.0\n" +
                    "gauge.int.value 4\n" +
                    "gauge.long.value 5\n" +
                    "gauge.null.value null\n" +
                    "gauge.short.value 3";

            assertEquals(expected, monitor.format(MetricsFilter.ALL_ENABLED, FIELDS_ALL_ENABLED));
        }
    }
}
