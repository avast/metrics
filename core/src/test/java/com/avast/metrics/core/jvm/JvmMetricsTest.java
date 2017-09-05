package com.avast.metrics.core.jvm;

import com.avast.metrics.api.Gauge;
import org.junit.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class JvmMetricsTest {
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    @Test
    public void testRegisterDefault() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerDefault(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            if (isWindows()) {
                // ManagementFactory.getOperatingSystemMXBean() is not UnixOperatingSystemMXBean
                // Number of open FDs is not supported
                assertEquals(20, gauges.size());
            } else {
                assertEquals(21, gauges.size());
            }
        }
    }

    @Test
    public void testRegisterCpu() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerDefault(monitor);

            assertThat((Long) monitor.findGauge("jvm.cpu.time").getValue())
                    .as("jvm.cpu.time")
                    .isPositive();

            assertThat((Double) monitor.findGauge("jvm.cpu.load").getValue())
                    .as("jvm.cpu.load")
                    .isPositive();
        }
    }

    @Test
    public void testRegisterFDs() throws Exception {
        if (isWindows()) {
            // ManagementFactory.getOperatingSystemMXBean() is not UnixOperatingSystemMXBean
            // Skip the test, not supported
            return;
        }

        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerDefault(monitor);

            assertThat((Long) monitor.findGauge("jvm.fds.opened").getValue())
                    .as("jvm.fds.opened")
                    .isBetween(0L, 1000L);
        }
    }

    @Test
    public void testRegisterHeap() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerDefault(monitor);

            assertThat((Long) monitor.findGauge("jvm.heap.used").getValue())
                    .as("jvm.heap.used")
                    .isPositive();

            assertThat((Long) monitor.findGauge("jvm.heap.committed").getValue())
                    .as("jvm.heap.committed")
                    .isPositive();

            assertThat((Long) monitor.findGauge("jvm.heap.max").getValue())
                    .as("jvm.heap.max")
                    .isPositive();
        }
    }

    @Test
    public void testRegisterNonHeap() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerDefault(monitor);

            assertThat((Long) monitor.findGauge("jvm.nonheap.used").getValue())
                    .as("jvm.nonheap.used")
                    .isPositive();

            assertThat((Long) monitor.findGauge("jvm.nonheap.committed").getValue())
                    .as("jvm.nonheap.committed")
                    .isPositive();
        }
    }

    @Test
    public void testRegisterUptime() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerDefault(monitor);

            assertThat((Long) monitor.findGauge("jvm.uptime").getValue())
                    .as("jvm.uptime")
                    .isBetween(0L, Duration.ofHours(1).toMillis());
        }
    }

    @Test
    public void testRegisterThreads() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerDefault(monitor);

            assertThat((Integer) monitor.findGauge("jvm.threads.total").getValue())
                    .as("jvm.threads.total")
                    .isBetween(0, 1000);

            assertThat((Integer) monitor.findGauge("jvm.threads.daemon").getValue())
                    .as("jvm.threads.daemon")
                    .isBetween(0, 1000);

            assertThat((Long) monitor.findGauge("jvm.threads.started").getValue())
                    .as("jvm.threads.started")
                    .isBetween(0L, 10000L);
        }
    }

    @Test
    public void testRegisterClasses() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerDefault(monitor);

            assertThat((Integer) monitor.findGauge("jvm.classes.loaded").getValue())
                    .as("jvm.classes.loaded")
                    .isBetween(0, 50000);
        }
    }

    @Test
    public void testRegisterBufferPools() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerDefault(monitor);

            assertThat((Long) monitor.findGauge("jvm.buffers.direct.instances").getValue())
                    .as("jvm.buffers.direct.instances")
                    .isBetween(0L, 1000L);

            assertThat((Long) monitor.findGauge("jvm.buffers.direct.bytes").getValue())
                    .as("jvm.buffers.direct.bytes")
                    .isBetween(0L, 20L * 1024L * 1024L);

            assertThat((Long) monitor.findGauge("jvm.buffers.mapped.instances").getValue())
                    .as("jvm.buffers.mapped.instances")
                    .isBetween(0L, 1000L);

            assertThat((Long) monitor.findGauge("jvm.buffers.mapped.bytes").getValue())
                    .as("jvm.buffers.mapped.bytes")
                    .isBetween(0L, 20L * 1024L * 1024L);
        }
    }

    @Test
    public void testRegisterGc() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerDefault(monitor);

            List<Gauge<?>> gauges = monitor.getGauges();


            List<Gauge<?>> gcCollections = gauges
                    .stream()
                    .filter(gauge -> gauge.getName().startsWith("jvm.gc.") && gauge.getName().endsWith(".collections"))
                    .collect(Collectors.toList());

            assertThat(gcCollections.size())
                    .as("GC collections size")
                    .isEqualTo(2);

            gcCollections.forEach(gauge ->
                    assertThat((Long) gauge.getValue())
                            .as(gauge.getName() + ", collections")
                            .isBetween(0L, 1000L));


            List<Gauge<?>> gcTimes = gauges
                    .stream()
                    .filter(gauge -> gauge.getName().startsWith("jvm.gc.") && gauge.getName().endsWith(".time"))
                    .collect(Collectors.toList());

            assertThat(gcTimes.size())
                    .as("GC times size")
                    .isEqualTo(2);

            gcTimes.forEach(gauge ->
                    assertThat((Long) gauge.getValue())
                            .as(gauge.getName() + ", time")
                            .isBetween(0L, Duration.ofSeconds(10).toMillis()));
        }
    }
}
