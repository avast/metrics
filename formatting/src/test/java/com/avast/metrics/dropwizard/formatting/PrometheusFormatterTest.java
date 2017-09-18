package com.avast.metrics.dropwizard.formatting;

import com.avast.metrics.filter.MetricsFilter;
import org.junit.Test;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class PrometheusFormatterTest {
    private static final PrometheusFormatter formatter = new PrometheusFormatter();

    @Test
    public void testSanitize() throws Exception {
        assertEquals(":abcdefgh:abcdefgh",
                formatter.sanitizeName(". :|@\n=()abcdefgh_. :|@\n=()abcdefgh_"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSanitizeEmpty() throws Exception {
        formatter.sanitizeName(""); // Exception
    }

    @Test
    public void testFormat() throws Exception {
        Stream<MetricValue> values = Stream.of(
                new MetricValue("name_a", "valueA"),
                new MetricValue("name_b", "valueB"),
                new MetricValue("name_c", "valueC")
        );

        String expected = "name_a valueA\n" +
                "name_b valueB\n" +
                "name_c valueC\n";

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

            String expected = "monitor_counter_count 0\n" +
                    "monitor_gauge_value 0\n" +
                    "monitor_histogram_count 0\n" +
                    "monitor_histogram_max 0\n" +
                    "monitor_histogram_mean 0.0\n" +
                    "monitor_histogram_min 0\n" +
                    "monitor_histogram_p50 0.0\n" +
                    "monitor_histogram_p99 0.0\n" +
                    "monitor_histogram_stddev 0.0\n" +
                    "monitor_meter_count 0\n" +
                    "monitor_meter_rate15m 0.0\n" +
                    "monitor_meter_rate1m 0.0\n" +
                    "monitor_meter_rate5m 0.0\n" +
                    "monitor_meter_ratemean 0.0\n" +
                    "monitor_timer_count 0\n" +
                    "monitor_timer_max 0\n" +
                    "monitor_timer_mean 0.0\n" +
                    "monitor_timer_min 0\n" +
                    "monitor_timer_p50 0.0\n" +
                    "monitor_timer_p99 0.0\n" +
                    "monitor_timer_rate15m 0.0\n" +
                    "monitor_timer_rate1m 0.0\n" +
                    "monitor_timer_rate5m 0.0\n" +
                    "monitor_timer_ratemean 0.0\n" +
                    "monitor_timer_stddev 0.0\n";

            assertEquals(expected, monitor.format(MetricsFilter.ALL_ENABLED, FormattingMetricsMonitorTest.FIELDS_ALL_ENABLED));
        }
    }

    @Test
    public void testLastLineEndsWithNewLineCharacter() throws Exception {
        // "The last line must end with a line-feed character."
        // https://prometheus.io/docs/instrumenting/exposition_formats/
        Stream<MetricValue> values = Stream.of(new MetricValue("name", "value"));
        assertEquals("name value\n", formatter.format(values));
    }

    @Test
    public void testNoMetric() throws Exception {
        // New line character is implementation specific, empty string would be correct too.
        // "Empty lines are ignored."
        // https://prometheus.io/docs/instrumenting/exposition_formats/
        Stream<MetricValue> values = Stream.empty();
        assertEquals("\n", formatter.format(values));
    }

    @Test
    public void testContentType() throws Exception {
        assertEquals("text/plain; version=0.0.4", formatter.contentType());
    }
}