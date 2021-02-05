package com.avast.metrics.statsd;

import com.avast.metrics.TimerPairImpl;
import com.avast.metrics.api.*;
import com.avast.metrics.filter.FilterConfig;
import com.avast.metrics.filter.MetricsFilter;
import com.avast.metrics.test.NoOpMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertTrue;

public class StatsDMetricFilterTest {

    private final Class noopMeterClazz = NoOpMonitor.INSTANCE.newMeter("testMonitor").getClass();
    private final Class noopCounterClazz = NoOpMonitor.INSTANCE.newCounter("testCounter").getClass();
    private final Class noopGaugeClazz = NoOpMonitor.INSTANCE.newGauge("testGauge", () -> "").getClass();
    private final Class noopHistogramClazz = NoOpMonitor.INSTANCE.newHistogram("testHistogram").getClass();
    private final Class noopTimerClazz = NoOpMonitor.INSTANCE.newTimer("testTimer").getClass();


    private ScheduledExecutorService scheduler;

    @Before
    public void setUp() {
        scheduler = Executors.newScheduledThreadPool(2);
    }

    @After
    public void tearDown() {
        scheduler.shutdown();
    }

    @Test
    public void testAllDisabled() {
        try (StatsDMetricsMonitor monitor = new StatsDMetricsMonitor("localhost", 80, "test-app", Naming.defaultNaming(), Duration.ofSeconds(1), scheduler, MetricsFilter.ALL_DISABLED)) {
            Meter meter = monitor.newMeter("meter");
            Counter counter = monitor.newCounter("counter");
            Gauge gauge = monitor.newGauge("gauge", () -> "");
            Histogram histogram = monitor.newHistogram("histogram");
            Timer timer = monitor.newTimer("timer");
            TimerPair timerPair = monitor.newTimerPair("timerPair");

            assertTrue(meter.getClass().equals(noopMeterClazz));
            assertTrue(counter.getClass().equals(noopCounterClazz));
            assertTrue(gauge.getClass().equals(noopGaugeClazz));
            assertTrue(histogram.getClass().equals(noopHistogramClazz));
            assertTrue(timer.getClass().equals(noopTimerClazz));
            assertTrue(timerPair.getClass().equals(TimerPairImpl.class)); // no-op are timers inside
        }
    }

    @Test
    public void testAllEnabled() {
        try (StatsDMetricsMonitor monitor = new StatsDMetricsMonitor("localhost", 80, "test-app", Naming.defaultNaming(), Duration.ofSeconds(1), scheduler, MetricsFilter.ALL_ENABLED)) {
            Meter meter = monitor.newMeter("meter");
            Counter counter = monitor.newCounter("counter");
            Gauge gauge = monitor.newGauge("gauge", () -> "");
            Histogram histogram = monitor.newHistogram("histogram");
            Timer timer = monitor.newTimer("timer");
            TimerPair timerPair = monitor.newTimerPair("timerPair");

            assertTrue(meter.getClass().equals(StatsDMeter.class));
            assertTrue(counter.getClass().equals(StatsDCounter.class));
            assertTrue(gauge.getClass().equals(StatsDGauge.class));
            assertTrue(histogram.getClass().equals(StatsDHistogram.class));
            assertTrue(timer.getClass().equals(StatsDTimer.class));
            assertTrue(timerPair.getClass().equals(TimerPairImpl.class));
        }
    }

    @Test
    public void testFiltered() {
        FilterConfig filterConfigRoot = new FilterConfig("root", true);
        FilterConfig filterConfig = new FilterConfig("level1.level2", false);
        MetricsFilter metricsFilter = buildMetricsFilter(filterConfigRoot, filterConfig);

        try (final StatsDMetricsMonitor monitor = buildMonitor(metricsFilter).named("level1")) {
            Meter meter = monitor.newMeter("level2");
            Counter counter = monitor.newCounter("level2");
            Gauge gauge = monitor.newGauge("level2", () -> "");
            Histogram histogram = monitor.newHistogram("level2");
            Timer timer = monitor.newTimer("level2");
            TimerPair timerPair = monitor.newTimerPair("level2");

            assertTrue(meter.getClass().equals(noopMeterClazz));
            assertTrue(counter.getClass().equals(noopCounterClazz));
            assertTrue(gauge.getClass().equals(noopGaugeClazz));
            assertTrue(histogram.getClass().equals(noopHistogramClazz));
            assertTrue(timer.getClass().equals(noopTimerClazz));
            assertTrue(timerPair.getClass().equals(TimerPairImpl.class)); // no-op are timers inside
        }
    }

    @Test
    public void testNotFiltered() {
        FilterConfig filterConfigRoot = new FilterConfig("root", false);
        FilterConfig filterConfig = new FilterConfig("level1.level2", true);
        MetricsFilter metricsFilter = buildMetricsFilter(filterConfigRoot, filterConfig);

        try (final StatsDMetricsMonitor monitor = buildMonitor(metricsFilter).named("level1")) {
            Meter meter = monitor.newMeter("level2");
            Counter counter = monitor.newCounter("level2");
            Gauge gauge = monitor.newGauge("level2", () -> "");
            Histogram histogram = monitor.newHistogram("level2");
            Timer timer = monitor.newTimer("level2");
            TimerPair timerPair = monitor.newTimerPair("level2");

            assertTrue(meter.getClass().equals(StatsDMeter.class));
            assertTrue(counter.getClass().equals(StatsDCounter.class));
            assertTrue(gauge.getClass().equals(StatsDGauge.class));
            assertTrue(histogram.getClass().equals(StatsDHistogram.class));
            assertTrue(timer.getClass().equals(StatsDTimer.class));
            assertTrue(timerPair.getClass().equals(TimerPairImpl.class));
        }
    }

    @Test
    public void testNestedMonitor() {
        FilterConfig filterConfigRoot = new FilterConfig("root", false);
        FilterConfig filterConfigMeter = new FilterConfig("level1.level2.meter", true);
        FilterConfig filterConfigCounter = new FilterConfig("level1.level2.counter", false);
        FilterConfig filterConfigGauge = new FilterConfig("level1.level2.gauge", true);
        FilterConfig filterConfigHistogram = new FilterConfig("level1.level2.histogram", false);
        FilterConfig filterConfigTimer = new FilterConfig("level1.level2.timer", true);
        FilterConfig filterConfigTimerPair = new FilterConfig("level1.level2.timePair", false);

        MetricsFilter metricsFilter = buildMetricsFilter(
                filterConfigRoot,
                filterConfigMeter,
                filterConfigCounter,
                filterConfigGauge,
                filterConfigHistogram,
                filterConfigTimer,
                filterConfigTimerPair
        );

        try (StatsDMetricsMonitor monitor = buildMonitor(metricsFilter).named("level1").named("level2")) {
            Meter meter = monitor.newMeter("meter");
            Counter counter = monitor.newCounter("counter");
            Gauge gauge = monitor.newGauge("gauge", () -> "");
            Histogram histogram = monitor.newHistogram("histogram");
            Timer timer = monitor.newTimer("timer");
            TimerPair timerPair = monitor.newTimerPair("timePair");

            assertTrue(meter.getClass().equals(StatsDMeter.class));
            assertTrue(counter.getClass().equals(noopCounterClazz));
            assertTrue(gauge.getClass().equals(StatsDGauge.class));
            assertTrue(histogram.getClass().equals(noopHistogramClazz));
            assertTrue(timer.getClass().equals(StatsDTimer.class));
            assertTrue(timerPair.getClass().equals(TimerPairImpl.class)); // no-op are timers inside
        }
    }

    private StatsDMetricsMonitor buildMonitor(MetricsFilter metricsFilter) {
        return new StatsDMetricsMonitor("localhost", 80, "test-app", Naming.defaultNaming(), Duration.ofSeconds(1), scheduler, metricsFilter);
    }

    private MetricsFilter buildMetricsFilter(FilterConfig... filterConfigs) {
        List<FilterConfig> filterConfigsList = new ArrayList<>();
        Collections.addAll(filterConfigsList, filterConfigs);
        return MetricsFilter.newInstance(filterConfigsList, ".");
    }
}
