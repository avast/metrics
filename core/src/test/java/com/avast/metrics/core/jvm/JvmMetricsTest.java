package com.avast.metrics.core.jvm;

import com.avast.metrics.api.Gauge;
import org.junit.Test;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JvmMetricsTest {
    private static final int OFFSET_CPU = 0;
    private static final int OFFSET_FDS = OFFSET_CPU + 2;
    private static final int OFFSET_HEAP = OFFSET_FDS + 2;
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

            assertEquals(22, gauges.size());
        }
    }

    @Test
    public void testRegisterCpu() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long cpuTime = (Long) gauges.get(OFFSET_CPU).getValue();
            assertEquals("jvm.cpu.time", gauges.get(OFFSET_CPU).getName());
            assertTrue("Process CPU time should be positive: " + cpuTime + " ms", cpuTime > 0);

            double cpuLoad = (Double) gauges.get(OFFSET_CPU + 1).getValue();
            assertEquals("jvm.cpu.load", gauges.get(OFFSET_CPU + 1).getName());
            assertTrue("Process CPU load should be positive: " + cpuLoad, cpuLoad > 0.0);
        }
    }

    @Test
    public void testRegisterFDs() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long fdsOpened = (Long) gauges.get(OFFSET_FDS).getValue();
            assertEquals("jvm.fds.opened", gauges.get(OFFSET_FDS).getName());
            assertTrue("Number of open file descriptors should be positive: " + fdsOpened, fdsOpened > 0);
            assertTrue("Number of open file descriptors should be low: " + fdsOpened, fdsOpened < 1000); // 25 now

            long fdsMax = (Long) gauges.get(OFFSET_FDS + 1).getValue();
            assertEquals("jvm.fds.max", gauges.get(OFFSET_FDS + 1).getName());
            assertTrue("Number of max file descriptors should be positive: " + fdsMax, fdsMax > 0);
            assertTrue("Number of max file descriptors should be low: " + fdsMax, fdsMax < 100000); // 25 now
        }
    }

    @Test
    public void testRegisterHeap() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long heapUsed = (Long) gauges.get(OFFSET_HEAP).getValue();
            assertEquals("jvm.heap.used", gauges.get(OFFSET_HEAP).getName());
            assertTrue("Used heap memory should be positive: " + heapUsed, heapUsed > 0);

            long heapCommitted = (Long) gauges.get(OFFSET_HEAP + 1).getValue();
            assertEquals("jvm.heap.committed", gauges.get(OFFSET_HEAP + 1).getName());
            assertTrue("Committed heap memory should be positive: " + heapCommitted, heapCommitted > 0);

            long heapMax = (Long) gauges.get(OFFSET_HEAP + 2).getValue();
            assertEquals("jvm.heap.max", gauges.get(OFFSET_HEAP + 2).getName());
            assertTrue("Max heap memory should be positive: " + heapMax, heapMax > 0);
        }
    }

    @Test
    public void testRegisterNonHeap() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long nonHeapUsed = (Long) gauges.get(OFFSET_NON_HEAP).getValue();
            assertEquals("jvm.nonheap.used", gauges.get(OFFSET_NON_HEAP).getName());
            assertTrue("Used non-heap memory should be positive: " + nonHeapUsed, nonHeapUsed > 0);

            long nonHeapCommitted = (Long) gauges.get(OFFSET_NON_HEAP + 1).getValue();
            assertEquals("jvm.nonheap.committed", gauges.get(OFFSET_NON_HEAP + 1).getName());
            assertTrue("Committed non-heap memory should be positive: " + nonHeapCommitted, nonHeapCommitted > 0);
        }
    }

    @Test
    public void testRegisterUptime() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long uptime = (Long) gauges.get(OFFSET_UPTIME).getValue();
            assertEquals("jvm.uptime", gauges.get(OFFSET_UPTIME).getName());
            assertTrue("Uptime should be positive: " + uptime + " ms", uptime > 0);
            assertTrue("Uptime should be shorter than 1 hour: " + uptime + " ms",
                    uptime <= Duration.ofHours(1).toMillis()); // Executed as part of unit tests which should be fast
        }
    }

    @Test
    public void testRegisterThreads() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            int totalThreads = (Integer) gauges.get(OFFSET_THREADS).getValue();
            assertEquals("jvm.threads.total", gauges.get(OFFSET_THREADS).getName());
            assertTrue("Total threads count should be positive: " + totalThreads, totalThreads > 0);
            assertTrue("Total threads count should be quite small: " + totalThreads, totalThreads < 100);

            int daemonThreads = (Integer) gauges.get(OFFSET_THREADS + 1).getValue();
            assertEquals("jvm.threads.daemon", gauges.get(OFFSET_THREADS + 1).getName());
            assertTrue("Daemon threads count should be positive: " + daemonThreads, daemonThreads > 0);
            assertTrue("Daemon threads count should be quite small: " + daemonThreads, daemonThreads < 100);

            long startedThreads = (Long) gauges.get(OFFSET_THREADS + 2).getValue();
            assertEquals("jvm.threads.started", gauges.get(OFFSET_THREADS + 2).getName());
            assertTrue("Started threads count should be positive: " + startedThreads, startedThreads > 0);
            assertTrue("Started threads count should be quite small: " + startedThreads, startedThreads < 500);
        }
    }

    @Test
    public void testRegisterClasses() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            int loadedClasses = (Integer) gauges.get(OFFSET_CLASSES).getValue();
            assertEquals("jvm.classes.loaded", gauges.get(OFFSET_CLASSES).getName());
            assertTrue("Loaded classes count should be positive: " + loadedClasses, loadedClasses > 0);
            assertTrue("Loaded classes count should be quite small: " + loadedClasses, loadedClasses < 50000);
        }
    }

    @Test
    public void testRegisterBufferPools() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long directBufferInstances = (Long) gauges.get(OFFSET_BUFFER_POOLS).getValue();
            assertEquals("jvm.buffers.direct.instances", gauges.get(OFFSET_BUFFER_POOLS).getName());
            assertTrue("Direct buffers count should be positive: " + directBufferInstances, directBufferInstances >= 0);
            assertTrue("Direct buffers count should be quite small: " + directBufferInstances, directBufferInstances < 100);

            long directBufferBytes = (Long) gauges.get(OFFSET_BUFFER_POOLS + 1).getValue();
            assertEquals("jvm.buffers.direct.bytes", gauges.get(OFFSET_BUFFER_POOLS + 1).getName());
            assertTrue("Direct buffers bytes should be positive: " + directBufferBytes, directBufferBytes >= 0);
            assertTrue("Direct buffers bytes should be quite small: " + directBufferBytes, directBufferBytes < 20 * 1024 * 1024);

            long mappedBufferInstances = (Long) gauges.get(OFFSET_BUFFER_POOLS + 2).getValue();
            assertEquals("jvm.buffers.mapped.instances", gauges.get(OFFSET_BUFFER_POOLS + 2).getName());
            assertTrue("Mapped buffers count should be positive: " + mappedBufferInstances, mappedBufferInstances >= 0);
            assertTrue("Mapped buffers count should be quite small: " + mappedBufferInstances, mappedBufferInstances < 100);

            long mappedBufferBytes = (Long) gauges.get(OFFSET_BUFFER_POOLS + 3).getValue();
            assertEquals("jvm.buffers.mapped.bytes", gauges.get(OFFSET_BUFFER_POOLS + 3).getName());
            assertTrue("Mapped buffers bytes should be positive: " + mappedBufferBytes, mappedBufferBytes >= 0);
            assertTrue("Mapped buffers bytes should be quite small: " + mappedBufferBytes, mappedBufferBytes < 20 * 1024 * 1024);
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
            assertTrue("GC A collections name should be possible: " + gauges.get(OFFSET_GC).getName(), gcCollectionNames.contains(gauges.get(OFFSET_GC).getName()));
            assertTrue("GC A collections should be positive: " + gcACollections, gcACollections >= 0);
            assertTrue("GC A collections should be quite small: " + gcACollections, gcACollections < 1000);

            long gcATime = (Long) gauges.get(OFFSET_GC + 1).getValue();
            assertTrue("GC A time name should be possible: " + gauges.get(OFFSET_GC).getName(), gcTimeNames.contains(gauges.get(OFFSET_GC + 1).getName()));
            assertTrue("GC A time should be positive: " + gcATime, gcATime >= 0);
            assertTrue("GC A time should be quite small: " + gcATime, gcATime < Duration.ofSeconds(10).toMillis());

            long gcBCollections = (Long) gauges.get(OFFSET_GC + 2).getValue();
            assertTrue("GC B collections name should be possible: " + gauges.get(OFFSET_GC).getName(), gcCollectionNames.contains(gauges.get(OFFSET_GC + 2).getName()));
            assertTrue("GC B collections should be positive: " + gcBCollections, gcBCollections >= 0);
            assertTrue("GC B collections should be quite small: " + gcBCollections, gcBCollections < 1000);

            long gcBTime = (Long) gauges.get(OFFSET_GC + 3).getValue();
            assertTrue("GC B time name should be possible: " + gauges.get(OFFSET_GC).getName(), gcTimeNames.contains(gauges.get(OFFSET_GC + 3).getName()));
            assertTrue("GC B time should be positive: " + gcBTime, gcBTime >= 0);
            assertTrue("GC B time should be quite small: " + gcBTime, gcBTime < Duration.ofSeconds(10).toMillis());
        }
    }
}
