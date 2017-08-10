package com.avast.metrics.core.jvm;

import com.avast.metrics.api.Gauge;
import org.junit.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JvmMetricsTest {
    @Test
    public void testRegisterAll() throws Exception {
        try (GaugesTestingMonitor monitor = new GaugesTestingMonitor()) {
            JvmMetrics.registerAll(monitor);
            List<Gauge<?>> gauges = monitor.getGauges();

            long cpuTime = (Long) gauges.get(0).getValue();
            assertEquals("jvm.cpu.time", gauges.get(0).getName());
            assertTrue("Process CPU time should be positive: " + cpuTime + " ms", cpuTime > 0);

            double cpuLoad = (Double) gauges.get(1).getValue();
            assertEquals("jvm.cpu.load", gauges.get(1).getName());
            assertTrue("Process CPU load should be positive: " + cpuLoad, cpuLoad > 0.0);

            long fds = (Long) gauges.get(2).getValue();
            assertEquals("jvm.fds", gauges.get(2).getName());
            assertTrue("Number of open file descriptors should be positive: " + fds, fds > 0);
            assertTrue("Number of open file descriptors should be low: " + fds, fds < 1000); // 25 now

            long heapUsed = (Long) gauges.get(3).getValue();
            assertEquals("jvm.heap.used", gauges.get(3).getName());
            assertTrue("Used heap memory should be positive: " + heapUsed, heapUsed > 0);

            long heapCommitted = (Long) gauges.get(4).getValue();
            assertEquals("jvm.heap.committed", gauges.get(4).getName());
            assertTrue("Committed heap memory should be positive: " + heapCommitted, heapCommitted > 0);

            long heapMax = (Long) gauges.get(5).getValue();
            assertEquals("jvm.heap.max", gauges.get(5).getName());
            assertTrue("Max heap memory should be positive: " + heapMax, heapMax > 0);

            long nonHeapUsed = (Long) gauges.get(6).getValue();
            assertEquals("jvm.nonheap.used", gauges.get(6).getName());
            assertTrue("Used non-heap memory should be positive: " + nonHeapUsed, nonHeapUsed > 0);

            long nonHeapCommitted = (Long) gauges.get(7).getValue();
            assertEquals("jvm.nonheap.committed", gauges.get(7).getName());
            assertTrue("Committed non-heap memory should be positive: " + nonHeapCommitted, nonHeapCommitted > 0);

            long uptime = (Long) gauges.get(8).getValue();
            assertEquals("jvm.uptime", gauges.get(8).getName());
            assertTrue("Uptime should be positive: " + uptime + " ms", uptime > 0);
            assertTrue("Uptime should be shorter than 1 hour: " + uptime + " ms",
                    uptime <= Duration.ofHours(1).toMillis()); // Executed as part of unit tests which should be fast

            int totalThreads = (Integer) gauges.get(9).getValue();
            assertEquals("jvm.threads.total", gauges.get(9).getName());
            assertTrue("Total threads count should be positive: " + totalThreads, totalThreads > 0);
            assertTrue("Total threads count should be quite small: " + totalThreads, totalThreads < 100);

            int daemonThreads = (Integer) gauges.get(10).getValue();
            assertEquals("jvm.threads.daemon", gauges.get(10).getName());
            assertTrue("Daemon threads count should be positive: " + daemonThreads, daemonThreads > 0);
            assertTrue("Daemon threads count should be quite small: " + daemonThreads, daemonThreads < 100);

            long startedThreads = (Long) gauges.get(11).getValue();
            assertEquals("jvm.threads.started", gauges.get(11).getName());
            assertTrue("Started threads count should be positive: " + startedThreads, startedThreads > 0);
            assertTrue("Started threads count should be quite small: " + startedThreads, startedThreads < 500);

            assertEquals(12, gauges.size());
        }
    }
}
