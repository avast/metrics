package com.avast.metrics.grpc;

import com.avast.metrics.api.Monitor;
import io.grpc.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class GrpcServerMonitoringInterceptor implements ServerInterceptor {
    private final MetricsCache cache;
    private final Clock clock;

    public GrpcServerMonitoringInterceptor(final Monitor monitor, final Clock clock) {
        this.clock = clock;
        cache = new MetricsCache(monitor);
    }

    public GrpcServerMonitoringInterceptor(final Monitor monitor) {
        this(monitor, Clock.systemUTC());
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, final Metadata headers, final ServerCallHandler<ReqT, RespT> next) {
        final String metricPrefix = MetricNaming.getMetricNamePrefix(call.getMethodDescriptor());
        final AtomicInteger currentCalls = cache.getGaugedValue(metricPrefix + "Current");

        final Instant start = clock.instant();
        currentCalls.incrementAndGet();

        final ServerCall<ReqT, RespT> newCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(final Status status, final Metadata trailers) {
                final Duration duration = Duration.between(start, clock.instant());
                currentCalls.decrementAndGet();

                if (ErrorCategory.fatal.contains(status.getCode())) {
                    cache.getTimer(metricPrefix + "FatalServerFailures")
                            .update(duration);
                } else if (ErrorCategory.client.contains(status.getCode())) {
                    cache.getTimer(metricPrefix + "ClientFailures")
                            .update(duration);
                } else if (status.isOk()) {
                    cache.getTimer(metricPrefix + "Successes")
                            .update(duration);
                } else {
                    cache.getTimer(metricPrefix + "ServerFailures")
                            .update(duration);
                }

                super.close(status, trailers);
            }
        };

        return next.startCall(newCall, headers);
    }
}
