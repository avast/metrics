package com.avast.metrics.grpc;

import com.avast.metrics.api.Monitor;
import io.grpc.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class GrpcServerMonitoringInterceptor implements ServerInterceptor {
    private final TimersCache timers;
    private final Clock clock;

    public GrpcServerMonitoringInterceptor(final Monitor monitor, final Clock clock) {
        this.clock = clock;
        timers = new TimersCache(monitor);
    }

    public GrpcServerMonitoringInterceptor(final Monitor monitor) {
        this(monitor, Clock.systemUTC());
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, final Metadata headers, final ServerCallHandler<ReqT, RespT> next) {
        final Instant start = clock.instant();

        final ServerCall<ReqT, RespT> newCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(final Status status, final Metadata trailers) {
                final Duration duration = Duration.between(start, clock.instant());

                if (status.isOk()) {
                    timers.get(call.getMethodDescriptor().getFullMethodName().replace('/', '_') + "Successes")
                            .update(duration);
                } else {
                    timers.get(call.getMethodDescriptor().getFullMethodName().replace('/', '_') + "Failures")
                            .update(duration);
                }

                super.close(status, trailers);
            }
        };

        return next.startCall(newCall, headers);
    }
}
