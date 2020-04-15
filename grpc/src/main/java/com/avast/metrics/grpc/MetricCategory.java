package com.avast.metrics.grpc;

import io.grpc.Status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MetricCategory {
    static Set<Status.Code> clientErrors = new HashSet<>(Arrays.asList(
            Status.Code.INVALID_ARGUMENT,
            Status.Code.UNAUTHENTICATED,
            Status.Code.PERMISSION_DENIED
    ));
}
