package com.avast.metrics.dropwizard.formatting;

import com.avast.metrics.filter.MetricsFilter;
import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.*;

public class PrometheusFormatterTest {
    private static final PrometheusFormatter formatter = new PrometheusFormatter();

    @Test
    public void testSanitize() throws Exception {
        assertEquals("XX:XXXXXXabcdefghXXX:XXXXXXabcdefghX",
                formatter.sanitizeName(". :|@\n=()abcdefgh_. :|@\n=()abcdefgh_"));
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
                "name_c valueC";

        assertEquals(expected, formatter.format(values));
    }

    @Test
    public void testWithFormattingMonitor() throws Exception {
        try (FormattingMetricsMonitor monitor = new FormattingMetricsMonitor(formatter)) {
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
                    "monitor_timer_stddev 0.0";

            assertEquals(expected, monitor.format(MetricsFilter.ALL_ENABLED, FormattingMetricsMonitorTest.FIELDS_ALL_ENABLED));
        }
    }
}