package com.avast.metrics.grpc;

import com.avast.metrics.api.Monitor;
import io.grpc.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class GrpcClientMonitoringInterceptor implements ClientInterceptor {
    private final TimersCache timers;
    private final Clock clock;

    public GrpcClientMonitoringInterceptor(final Monitor monitor, final Clock clock) {
        this.clock = clock;
        timers = new TimersCache(monitor);
    }

    public GrpcClientMonitoringInterceptor(final Monitor monitor) {
        this(monitor, Clock.systemUTC());
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> method, final CallOptions callOptions, final Channel next) {
        final Instant start = clock.instant();

        final ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                delegate().start(
                        new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                            @Override
                            public void onClose(io.grpc.Status status, Metadata trailers) {
                                final Duration duration = Duration.between(start, clock.instant());

                                if (status.isOk()) {
                                    timers.get(method.getFullMethodName().replace("/", "_") + "Successes")
                                            .update(duration);
                                } else {
                                    timers.get(method.getFullMethodName().replace("/", "_") + "Failures")
                                            .update(duration);
                                }

                                super.onClose(status, trailers);
                            }
                        },
                        headers);
            }
        };
    }
}
