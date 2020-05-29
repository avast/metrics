package com.avast.metrics.grpc;


import io.grpc.MethodDescriptor;

public class MetricNaming {
    private MetricNaming() {}

    public static <ReqT, RespT>String getMethodMonitorName(MethodDescriptor<ReqT, RespT> methodDescriptor) {
        return methodDescriptor.getFullMethodName().replace('/', '_');
    }
}
