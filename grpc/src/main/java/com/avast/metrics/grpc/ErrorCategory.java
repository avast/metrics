package com.avast.metrics.grpc;

import io.grpc.Status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ErrorCategory {
    static Set<Status.Code> client = new HashSet<>(Arrays.asList(
            Status.Code.INVALID_ARGUMENT,
            Status.Code.UNAUTHENTICATED,
            Status.Code.PERMISSION_DENIED
    ));
    static Set<Status.Code> fatal = new HashSet<>(Arrays.asList(
            Status.Code.INTERNAL,
            Status.Code.UNKNOWN
    ));
}
