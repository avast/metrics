package com.avast.metrics.dropwizard.formatting;

import com.avast.metrics.filter.MetricsFilter;
import org.junit.Test;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class GraphiteFormatterTest {
    private static final GraphiteFormatter formatter = new GraphiteFormatter();

    @Test
    public void testSanitize() throws Exception {
        assertEquals("-_-------abcdefgh-_-------abcdefgh",
                formatter.sanitizeName(". :|@\n=()abcdefgh. :|@\n=()abcdefgh"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSanitizeEmpty() throws Exception {
        formatter.sanitizeName(""); // Exception
    }

    @Test
    public void testFormat() throws Exception {
        Stream<MetricValue> values = Stream.of(
                new MetricValue("name.a", "valueA"),
                new MetricValue("name.b", "valueB"),
                new MetricValue("name.c", "valueC")
        );

        String expected = "name.a valueA\n" +
                "name.b valueB\n" +
                "name.c valueC";

        assertEquals(expected, formatter.format(values));
    }

    @Test
    public void testWithFormattingMonitor() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(formatter, Collections.emptyList())) {
            monitor.named("monitor").newCounter("counter");
            monitor.named("monitor").newGauge("gauge", () -> 0);
            monitor.named("monitor").newMeter("meter");
            monitor.named("monitor").newTimer("timer");
            monitor.named("monitor").newHistogram("histogram");

            String expected = "monitor.counter.count 0\n" +
                    "monitor.gauge.value 0\n" +
                    "monitor.histogram.count 0\n" +
                    "monitor.histogram.max 0\n" +
                    "monitor.histogram.mean 0.0\n" +
                    "monitor.histogram.min 0\n" +
                    "monitor.histogram.p50 0.0\n" +
                    "monitor.histogram.p99 0.0\n" +
                    "monitor.histogram.stddev 0.0\n" +
                    "monitor.meter.count 0\n" +
                    "monitor.meter.rate15m 0.0\n" +
                    "monitor.meter.rate1m 0.0\n" +
                    "monitor.meter.rate5m 0.0\n" +
                    "monitor.meter.ratemean 0.0\n" +
                    "monitor.timer.count 0\n" +
                    "monitor.timer.max 0\n" +
                    "monitor.timer.mean 0.0\n" +
                    "monitor.timer.min 0\n" +
                    "monitor.timer.p50 0.0\n" +
                    "monitor.timer.p99 0.0\n" +
                    "monitor.timer.rate15m 0.0\n" +
                    "monitor.timer.rate1m 0.0\n" +
                    "monitor.timer.rate5m 0.0\n" +
                    "monitor.timer.ratemean 0.0\n" +
                    "monitor.timer.stddev 0.0";

            assertEquals(expected, monitor.format(MetricsFilter.ALL_ENABLED, FormattingMetricsMonitorTest.FIELDS_ALL_ENABLED));
        }
    }

    @Test
    public void testNoMetric() throws Exception {
        Stream<MetricValue> values = Stream.empty();
        assertEquals("", formatter.format(values));
    }

    @Test
    public void testContentType() throws Exception {
        assertEquals("text/plain", formatter.contentType());
    }
}
