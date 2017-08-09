package com.avast.metrics.core.jvm;

import com.avast.metrics.api.Gauge;
import com.avast.metrics.test.NoOpMonitor;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JvmMetricsTest {
    @Test
    public void testRegisterAll() throws Exception {
        JvmMetrics.registerAll(NoOpMonitor.INSTANCE);
    }

    @Test
    public void testRegisterProcessCpuTime() throws Exception {
        Gauge<Long> gauge = JvmMetrics.registerProcessCpuTime(NoOpMonitor.INSTANCE);
        long cpuTime = gauge.getValue();

        assertEquals("cputime", gauge.getName());
        assertTrue("Process CPU time should be positive: " + cpuTime + " ms", cpuTime > 0);
    }

    @Test
    public void testRegisterProcessCpuLoad() throws Exception {
        Gauge<Double> gauge = JvmMetrics.registerProcessCpuLoad(NoOpMonitor.INSTANCE);
        double load = gauge.getValue();

        assertEquals("cpuload", gauge.getName());
        assertTrue("Process CPU load should be positive: " + load, load > 0.0);
    }

    @Test
    public void testRegisterOpenFDsCount() throws Exception {
        Gauge<Long> gauge = JvmMetrics.registerOpenFDsCount(NoOpMonitor.INSTANCE);
        long fds = gauge.getValue();

        assertEquals("openfds", gauge.getName());
        assertTrue("Number of open file descriptors should be positive: " + fds, fds > 0);
        assertTrue("Number of open file descriptors should be low: " + fds, fds < 1000); // 25 now
    }

    @Test
    public void testRegisterProcessUptime() throws Exception {
        Gauge<Long> gauge = JvmMetrics.registerProcessUptime(NoOpMonitor.INSTANCE);
        long uptime = gauge.getValue();

        assertEquals("uptime", gauge.getName());
        assertTrue("Uptime should be positive: " + uptime + " ms", uptime > 0);
        assertTrue("Uptime should be shorter than 1 hour: " + uptime + " ms",
                uptime <= Duration.ofHours(1).toMillis()); // Executed as part of unit tests which should be fast
    }
}
