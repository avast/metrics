package com.avast.metrics.core.jvm;

import com.avast.metrics.api.Gauge;
import com.avast.metrics.api.Monitor;
import com.avast.metrics.test.NoOpMonitor;
import com.sun.management.UnixOperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

/**
 * Reporter of common JVM-layer metrics.
 */
@SuppressWarnings("WeakerAccess")
public class JvmMetrics {
    protected static final Logger LOGGER = LoggerFactory.getLogger(JvmMetrics.class);

    public static void registerAll(Monitor monitor) {
        Monitor jvmMonitor = monitor.named("jvm");
        registerProcessCpuTime(jvmMonitor);
        registerProcessCpuLoad(jvmMonitor);
        registerOpenFDsCount(jvmMonitor);
        registerProcessUptime(jvmMonitor);
    }

    /**
     * Returns the CPU time used by the process on which the Java virtual machine is running in nanoseconds.
     */
    public static Gauge<Long> registerProcessCpuTime(Monitor monitor) {
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();

        if (bean instanceof com.sun.management.OperatingSystemMXBean) {
            return monitor.newGauge("cputime", ((com.sun.management.OperatingSystemMXBean) bean)::getProcessCpuTime);
        } else {
            LOGGER.warn("Reading of process CPU time failed, there may be changes in JVM internals: {}", bean.getClass());
            return NoOpMonitor.INSTANCE.newGauge("cputime", () -> -1L);
        }
    }

    /**
     * Returns the "recent cpu usage" for the Java Virtual Machine process.
     */
    public static Gauge<Double> registerProcessCpuLoad(Monitor monitor) {
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();

        if (bean instanceof com.sun.management.OperatingSystemMXBean) {
            // There are multiple possibilities with very different results
            // getProcessCpuLoad()    7.307818620526464E-6
            // getSystemCpuLoad()     0.07893744901883035
            // getSystemLoadAverage() 0.45 (same value as from unix uptime command)
            return monitor.newGauge("cpuload", ((com.sun.management.OperatingSystemMXBean) bean)::getProcessCpuLoad);
        } else {
            LOGGER.warn("Reading of process CPU time failed, there may be changes in JVM internals: {}", bean.getClass());
            return NoOpMonitor.INSTANCE.newGauge("cpuload", () -> -1.0);
        }
    }

    public static Gauge<Long> registerOpenFDsCount(Monitor monitor) {
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();

        if (bean instanceof UnixOperatingSystemMXBean) {
            System.err.println(((UnixOperatingSystemMXBean) bean).getOpenFileDescriptorCount());
            return monitor.newGauge("openfds", ((UnixOperatingSystemMXBean) bean)::getOpenFileDescriptorCount);
        } else {
            LOGGER.warn("Reading of open FDs failed, there may be changes in JVM internals: {}", bean.getClass());
            return NoOpMonitor.INSTANCE.newGauge("openfds", () -> -1L);
        }
    }

    /**
     * JVM uptime in milliseconds.
     */
    public static Gauge<Long> registerProcessUptime(Monitor monitor) {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        return monitor.newGauge("uptime", bean::getUptime);
    }
}
