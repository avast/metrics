package com.avast.metrics.grpc;

import com.avast.metrics.api.Monitor;
import io.grpc.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class GrpcServerMonitoringInterceptor implements ServerInterceptor {
    private final Monitor monitor;
    private final Clock clock;

    public GrpcServerMonitoringInterceptor(final Monitor monitor, final Clock clock) {
        this.monitor = monitor;
        this.clock = clock;
    }

    protected GrpcServerMonitoringInterceptor(final Monitor monitor) {
        this.monitor = monitor;
        this.clock = Clock.systemUTC();
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, final Metadata headers, final ServerCallHandler<ReqT, RespT> next) {
        final Instant start = clock.instant();

        final ServerCall<ReqT, RespT> newCall = new ServerCall<ReqT, RespT>() {
            @Override
            public void request(final int numMessages) {
                call.request(numMessages);
            }

            @Override
            public void sendHeaders(final Metadata headers) {
                call.sendHeaders(headers);
            }

            @Override
            public void sendMessage(final RespT message) {
                call.sendMessage(message);
            }

            @Override
            public void close(final Status status, final Metadata trailers) {
                final Duration duration = Duration.between(start, clock.instant());

                if (status.isOk()) {
                    monitor.newTimer(call.getMethodDescriptor().getFullMethodName().replace("/", "_") + "Successes")
                            .update(duration);
                } else {
                    monitor.newTimer(call.getMethodDescriptor().getFullMethodName().replace("/", "_") + "Failures")
                            .update(duration);
                }

                call.close(status, trailers);
            }

            @Override
            public boolean isCancelled() {
                return call.isCancelled();
            }

            @Override
            public MethodDescriptor<ReqT, RespT> getMethodDescriptor() {
                return call.getMethodDescriptor();
            }
        };

        return next.startCall(newCall, headers);
    }
}
