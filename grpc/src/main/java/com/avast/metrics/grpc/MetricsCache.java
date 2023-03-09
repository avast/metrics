package com.avast.metrics.grpc;

import com.avast.metrics.api.Meter;
import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Timer;
import io.grpc.MethodDescriptor;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class MetricsCache {
    private final Monitor monitor;
    private final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Meter> meters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> gaugedValues = new ConcurrentHashMap<>();

    public MetricsCache(final Monitor monitor) {
        this.monitor = monitor;
    }

    public <ReqT, RespT> Timer getTimer(MethodDescriptor<ReqT, RespT> methodDescriptor, String name) {
        String methodMonitorName = MetricNaming.getMethodMonitorName(methodDescriptor);
        return timers.computeIfAbsent(methodMonitorName + name, (s) -> monitor.named(methodMonitorName).newTimer(name));
    }

    public <ReqT, RespT> Meter getMeter(MethodDescriptor<ReqT, RespT> methodDescriptor, String name) {
        String methodMonitorName = MetricNaming.getMethodMonitorName(methodDescriptor);
        return meters.computeIfAbsent(methodMonitorName + name, (s) -> monitor.named(methodMonitorName).newMeter(name));
    }

    public <ReqT, RespT> Meter getMeter(MethodDescriptor<ReqT, RespT> methodDescriptor, String name1, String name2) {
        String methodMonitorName = MetricNaming.getMethodMonitorName(methodDescriptor);
        return meters.computeIfAbsent(methodMonitorName + name1 + name2, (s) -> monitor.named(methodMonitorName, name1).newMeter(name2));
    }

    public <ReqT, RespT> AtomicInteger getGaugedValue(MethodDescriptor<ReqT, RespT> methodDescriptor, String name) {
        String methodMonitorName = MetricNaming.getMethodMonitorName(methodDescriptor);
        return gaugedValues.computeIfAbsent(methodMonitorName + name, n -> {
            AtomicInteger v = new AtomicInteger();
            monitor.named(methodMonitorName).newGauge(name, v::get);
            return v;
        });
    }

    public <ReqT, RespT> AtomicInteger getGaugedValueByHostname(MethodDescriptor<ReqT, RespT> methodDescriptor, String name) {
        String methodMonitorName = MetricNaming.getMethodMonitorName(methodDescriptor);
        return gaugedValues.computeIfAbsent(methodMonitorName + name + MetricNaming.getHostname(), n -> {
            AtomicInteger v = new AtomicInteger();
            monitor.named(methodMonitorName, name).newGauge(MetricNaming.getHostname(), v::get);
            return v;
        });
    }
}
