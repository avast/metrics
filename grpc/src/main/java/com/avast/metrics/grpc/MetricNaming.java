package com.avast.metrics.grpc;


import io.grpc.MethodDescriptor;

class MetricNaming {
    private MetricNaming() {}

    static <ReqT, RespT>String getMetricNamePrefix(MethodDescriptor<ReqT, RespT> methodDescriptor) {
        return methodDescriptor.getFullMethodName().replace('/', '_');
    }
}
