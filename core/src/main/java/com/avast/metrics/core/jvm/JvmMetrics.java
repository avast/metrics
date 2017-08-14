package com.avast.metrics.core.jvm;

import com.avast.metrics.api.Monitor;
import com.sun.management.UnixOperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.ManagementFactoryHelper;

import java.lang.management.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reporter of common JVM-layer metrics.
 */
@SuppressWarnings("WeakerAccess")
public class JvmMetrics {
    private static final Logger LOGGER = LoggerFactory.getLogger(JvmMetrics.class);

    static final Map<String, String> GC_NAMES_MAPPING = new HashMap<>();

    static {
        // young
        GC_NAMES_MAPPING.put("Copy", "copy"); // -XX:+UseSerialGC
        GC_NAMES_MAPPING.put("PS Scavenge", "psscav"); // -XX:+UseParallelGC
        GC_NAMES_MAPPING.put("ParNew", "parnew"); // -XX:+UseParNewGC
        GC_NAMES_MAPPING.put("G1 Young Generation", "g1young"); // -XX:+UseG1GC

        // old
        GC_NAMES_MAPPING.put("MarkSweepCompact", "msc"); // -XX:+UseSerialGC
        GC_NAMES_MAPPING.put("PS MarkSweep", "psms"); // -XX:+UseParallelOldGC
        GC_NAMES_MAPPING.put("ConcurrentMarkSweep", "conms"); // -XX:+UseConcMarkSweepGC
        GC_NAMES_MAPPING.put("G1 Old Generation", "g1old"); // -XX:+UseG1GC
    }

    public static void registerAll(Monitor monitor) {
        Monitor jvmMonitor = monitor.named("jvm");

        registerCpu(jvmMonitor.named("cpu"));
        registerFDs(jvmMonitor.named("fds"));
        registerHeapMemory(jvmMonitor.named("heap"));
        registerNonHeapMemory(jvmMonitor.named("nonheap"));
        registerProcessUptime(jvmMonitor);
        registerThreads(jvmMonitor.named("threads"));
        registerClasses(jvmMonitor.named("classes"));
        registerBufferPools(jvmMonitor.named("buffers"));
        registerGc(jvmMonitor.named("gc"));
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
     * Number of file descriptors.
     */
    private static void registerFDs(Monitor monitor) {
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();

        if (bean instanceof UnixOperatingSystemMXBean) {
            monitor.newGauge("opened", ((UnixOperatingSystemMXBean) bean)::getOpenFileDescriptorCount);
        } else {
            LOGGER.warn("Registration of file descriptors count failed, there may be changes in JVM internals: {}", bean.getClass());
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

    /**
     * Garbage collectors.
     */
    private static void registerGc(Monitor monitor) {
        List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();

        beans.forEach(bean -> {
            Monitor subMonitor = monitor.named(mapGcName(bean.getName()));
            subMonitor.newGauge("collections", bean::getCollectionCount);
            subMonitor.newGauge("time", bean::getCollectionTime);
        });
    }

    private static String mapGcName(String name) {
        return GC_NAMES_MAPPING.getOrDefault(name, name.replace(" ", ""));
    }
}
