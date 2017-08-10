package com.avast.metrics.core.jvm;

import com.avast.metrics.api.Gauge;
import org.junit.Test;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class JvmMetricsTest {
    private static final int OFFSET_CPU = 0;
    private static final int OFFSET_FDS = OFFSET_CPU + 2;
    private static final int OFFSET_HEAP = OFFSET_FDS + 1;
    private static final int OFFSET_NON_HEAP = OFFSET_HEAP + 3;
    private static final int OFFSET_UPTIME = OFFSET_NON_HEAP + 2;
    private static final int OFFSET_THREADS = OFFSET_UPTIME + 1;
    private static final int OFFSET_CLASSES = OFFSET_THREADS + 3;
    private static final int OFFSET_BUFFER_POOLS = OFFSET_CLASSES + 1;
    private static final int OFFSET_GC = OFFSET_BUFFER_POOLS + 4;

    @Test
    public void testRegisterAll() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            assertEquals(21, gauges.size());
        }
    }

    @Test
    public void testRegisterCpu() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long cpuTime = (Long) gauges.get(OFFSET_CPU).getValue();
            assertEquals("jvm.cpu.time", gauges.get(OFFSET_CPU).getName());
            assertThat(cpuTime).as("CPU time").isPositive();

            double cpuLoad = (Double) gauges.get(OFFSET_CPU + 1).getValue();
            assertEquals("jvm.cpu.load", gauges.get(OFFSET_CPU + 1).getName());
            assertThat(cpuLoad).as("CPU load").isPositive();
        }
    }

    @Test
    public void testRegisterFDs() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long fdsOpened = (Long) gauges.get(OFFSET_FDS).getValue();
            assertEquals("jvm.fds.opened", gauges.get(OFFSET_FDS).getName());
            assertThat(fdsOpened).as("opened FDs").isBetween(0L, 1000L);
        }
    }

    @Test
    public void testRegisterHeap() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long heapUsed = (Long) gauges.get(OFFSET_HEAP).getValue();
            assertEquals("jvm.heap.used", gauges.get(OFFSET_HEAP).getName());
            assertThat(heapUsed).as("used heap memory").isPositive();

            long heapCommitted = (Long) gauges.get(OFFSET_HEAP + 1).getValue();
            assertEquals("jvm.heap.committed", gauges.get(OFFSET_HEAP + 1).getName());
            assertThat(heapCommitted).as("committed heap memory").isPositive();

            long heapMax = (Long) gauges.get(OFFSET_HEAP + 2).getValue();
            assertEquals("jvm.heap.max", gauges.get(OFFSET_HEAP + 2).getName());
            assertThat(heapMax).as("max heap memory").isPositive();
        }
    }

    @Test
    public void testRegisterNonHeap() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long nonHeapUsed = (Long) gauges.get(OFFSET_NON_HEAP).getValue();
            assertEquals("jvm.nonheap.used", gauges.get(OFFSET_NON_HEAP).getName());
            assertThat(nonHeapUsed).as("used non heap memory").isPositive();

            long nonHeapCommitted = (Long) gauges.get(OFFSET_NON_HEAP + 1).getValue();
            assertEquals("jvm.nonheap.committed", gauges.get(OFFSET_NON_HEAP + 1).getName());
            assertThat(nonHeapCommitted).as("committed non heap memory").isPositive();
        }
    }

    @Test
    public void testRegisterUptime() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long uptime = (Long) gauges.get(OFFSET_UPTIME).getValue();
            assertEquals("jvm.uptime", gauges.get(OFFSET_UPTIME).getName());
            assertThat(uptime).as("uptime").isBetween(0L, Duration.ofHours(1).toMillis());
        }
    }

    @Test
    public void testRegisterThreads() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            int totalThreads = (Integer) gauges.get(OFFSET_THREADS).getValue();
            assertEquals("jvm.threads.total", gauges.get(OFFSET_THREADS).getName());
            assertThat(totalThreads).as("total threads").isBetween(0, 100);

            int daemonThreads = (Integer) gauges.get(OFFSET_THREADS + 1).getValue();
            assertEquals("jvm.threads.daemon", gauges.get(OFFSET_THREADS + 1).getName());
            assertThat(daemonThreads).as("daemon threads").isBetween(0, 100);

            long startedThreads = (Long) gauges.get(OFFSET_THREADS + 2).getValue();
            assertEquals("jvm.threads.started", gauges.get(OFFSET_THREADS + 2).getName());
            assertThat(startedThreads).as("started threads").isBetween(0L, 500L);
        }
    }

    @Test
    public void testRegisterClasses() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            int loadedClasses = (Integer) gauges.get(OFFSET_CLASSES).getValue();
            assertEquals("jvm.classes.loaded", gauges.get(OFFSET_CLASSES).getName());
            assertThat(loadedClasses).as("loaded classes").isBetween(0, 50000);
        }
    }

    @Test
    public void testRegisterBufferPools() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long directBufferInstances = (Long) gauges.get(OFFSET_BUFFER_POOLS).getValue();
            assertEquals("jvm.buffers.direct.instances", gauges.get(OFFSET_BUFFER_POOLS).getName());
            assertThat(directBufferInstances).as("direct buffer instances").isBetween(0L, 100L);

            long directBufferBytes = (Long) gauges.get(OFFSET_BUFFER_POOLS + 1).getValue();
            assertEquals("jvm.buffers.direct.bytes", gauges.get(OFFSET_BUFFER_POOLS + 1).getName());
            assertThat(directBufferBytes).as("direct buffer bytes").isBetween(0L, 20L * 1024L * 1024L);

            long mappedBufferInstances = (Long) gauges.get(OFFSET_BUFFER_POOLS + 2).getValue();
            assertEquals("jvm.buffers.mapped.instances", gauges.get(OFFSET_BUFFER_POOLS + 2).getName());
            assertThat(mappedBufferInstances).as("mapped buffer instances").isBetween(0L, 100L);

            long mappedBufferBytes = (Long) gauges.get(OFFSET_BUFFER_POOLS + 3).getValue();
            assertEquals("jvm.buffers.mapped.bytes", gauges.get(OFFSET_BUFFER_POOLS + 3).getName());
            assertThat(mappedBufferBytes).as("mapped buffer bytes").isBetween(0L, 20L * 1024L * 1024L);
        }
    }

    @Test
    public void testRegisterGc() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            Set<String> gcCollectionNames = JvmMetrics.GC_NAMES_MAPPING
                    .values()
                    .stream()
                    .map(name -> "jvm.gc." + name + ".collections")
                    .collect(Collectors.toSet());

            Set<String> gcTimeNames = JvmMetrics.GC_NAMES_MAPPING
                    .values()
                    .stream()
                    .map(name -> "jvm.gc." + name + ".time")
                    .collect(Collectors.toSet());

            long gcACollections = (Long) gauges.get(OFFSET_GC).getValue();
            assertThat(gcCollectionNames).contains(gauges.get(OFFSET_GC).getName());
            assertThat(gcACollections).as("GC A collections").isBetween(0L, 1000L);

            long gcATime = (Long) gauges.get(OFFSET_GC + 1).getValue();
            assertThat(gcTimeNames).contains(gauges.get(OFFSET_GC + 1).getName());
            assertThat(gcATime).as("GC A time").isBetween(0L, Duration.ofSeconds(10).toMillis());

            long gcBCollections = (Long) gauges.get(OFFSET_GC + 2).getValue();
            assertThat(gcCollectionNames).contains(gauges.get(OFFSET_GC + 2).getName());
            assertThat(gcBCollections).as("GC B collections").isBetween(0L, 1000L);

            long gcBTime = (Long) gauges.get(OFFSET_GC + 3).getValue();
            assertThat(gcTimeNames).contains(gauges.get(OFFSET_GC + 3).getName());
            assertThat(gcBTime).as("GC B time").isBetween(0L, Duration.ofSeconds(10).toMillis());
        }
    }
}
