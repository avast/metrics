package com.avast.metrics.core.jvm;

import com.avast.metrics.api.Monitor;
import com.sun.management.UnixOperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
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

        registerCpu(jvmMonitor.named("cpu"));
        registerFDsCount(jvmMonitor);
        registerHeapMemory(jvmMonitor.named("heap"));
        registerProcessUptime(jvmMonitor);
    }

    /**
     * CPU time (nanoseconds) and load.
     */
    private static void registerCpu(Monitor monitor) {
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();

        if (bean instanceof com.sun.management.OperatingSystemMXBean) {
            monitor.newGauge("time", ((com.sun.management.OperatingSystemMXBean) bean)::getProcessCpuTime);

            // There are multiple possibilities with very different results
            // getProcessCpuLoad()    7.307818620526464E-6
            // getSystemCpuLoad()     0.07893744901883035
            // getSystemLoadAverage() 0.45 (same value as from unix uptime command)
            monitor.newGauge("load", ((com.sun.management.OperatingSystemMXBean) bean)::getProcessCpuLoad);
        } else {
            LOGGER.warn("Registration of process CPU metrics failed, there may be changes in JVM internals: {}", bean.getClass());
        }
    }

    /**
     * Number of open file descriptors.
     */
    private static void registerFDsCount(Monitor monitor) {
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();

        if (bean instanceof UnixOperatingSystemMXBean) {
            monitor.newGauge("fds", ((UnixOperatingSystemMXBean) bean)::getOpenFileDescriptorCount);
        } else {
            LOGGER.warn("Registration of open FDs count failed, there may be changes in JVM internals: {}", bean.getClass());
        }
    }

    /**
     * Heap memory in bytes.
     */
    private static void registerHeapMemory(Monitor monitor) {
        MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        monitor.newGauge("used", () -> bean.getHeapMemoryUsage().getUsed());
        monitor.newGauge("committed", () -> bean.getHeapMemoryUsage().getCommitted());
        monitor.newGauge("max", () -> bean.getHeapMemoryUsage().getMax());
    }

    /**
     * Uptime in milliseconds.
     */
    private static void registerProcessUptime(Monitor monitor) {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        monitor.newGauge("uptime", bean::getUptime);
    }
}
