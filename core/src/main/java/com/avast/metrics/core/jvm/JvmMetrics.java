package com.avast.metrics.core.jvm;

import com.avast.metrics.api.Monitor;
import com.sun.management.UnixOperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.ManagementFactoryHelper;

import java.lang.management.*;
import java.util.List;

/**
 * Reporter of common JVM-layer metrics.
 */
@SuppressWarnings("WeakerAccess")
public class JvmMetrics {
    protected static final Logger LOGGER = LoggerFactory.getLogger(JvmMetrics.class);

    public static void registerAll(Monitor monitor) {
        Monitor jvmMonitor = monitor.named("jvm");

        registerCpu(jvmMonitor.named("cpu"));
        registerFDsCount(jvmMonitor.named("fds"));
        registerHeapMemory(jvmMonitor.named("heap"));
        registerNonHeapMemory(jvmMonitor.named("nonheap"));
        registerProcessUptime(jvmMonitor);
        registerThreads(jvmMonitor.named("threads"));
        registerClasses(jvmMonitor.named("classes"));
        registerBufferPools(jvmMonitor.named("buffers"));
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
            monitor.newGauge("opened", ((UnixOperatingSystemMXBean) bean)::getOpenFileDescriptorCount);
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
     * Non heap memory in bytes.
     */
    private static void registerNonHeapMemory(Monitor monitor) {
        MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        monitor.newGauge("used", () -> bean.getNonHeapMemoryUsage().getUsed());
        monitor.newGauge("committed", () -> bean.getNonHeapMemoryUsage().getCommitted());
    }

    /**
     * Uptime in milliseconds.
     */
    private static void registerProcessUptime(Monitor monitor) {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        monitor.newGauge("uptime", bean::getUptime);
    }

    /**
     * Number of threads.
     */
    private static void registerThreads(Monitor monitor) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        monitor.newGauge("total", bean::getThreadCount); // Both daemon and non-daemon
        monitor.newGauge("daemon", bean::getDaemonThreadCount);
        monitor.newGauge("started", bean::getTotalStartedThreadCount); // Since JVM started
    }

    /**
     * Number of classes.
     */
    private static void registerClasses(Monitor monitor) {
        ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();
        monitor.newGauge("loaded", bean::getLoadedClassCount); // Currently loaded
    }

    /**
     * Number of buffer pools.
     */
    private static void registerBufferPools(Monitor monitor) {
        List<BufferPoolMXBean> beans = ManagementFactoryHelper.getBufferPoolMXBeans();

        beans.forEach(bean -> {
            Monitor subMonitor = monitor.named(bean.getName()); // "direct" and "mapped"
            subMonitor.newGauge("instances", bean::getCount);
            subMonitor.newGauge("bytes", bean::getMemoryUsed);
        });
    }
}
