package com.avast.metrics.grpc;

import com.avast.metrics.api.Monitor;
import io.grpc.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class GrpcClientMonitoringInterceptor implements ClientInterceptor {
    private final MetricsCache cache;
    private final Clock clock;

    public GrpcClientMonitoringInterceptor(final Monitor monitor, final Clock clock) {
        this.clock = clock;
        cache = new MetricsCache(monitor);
    }

    public GrpcClientMonitoringInterceptor(final Monitor monitor) {
        this(monitor, Clock.systemUTC());
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> method, final CallOptions callOptions, final Channel next) {
        final String metricPrefix = MetricNaming.getMetricNamePrefix(method);
        final AtomicInteger currentCalls = cache.getGaugedValue(metricPrefix + "Current");

        final Instant start = clock.instant();
        currentCalls.incrementAndGet();
        final ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                delegate().start(
                        new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                            @Override
                            public void onClose(io.grpc.Status status, Metadata trailers) {
                                final Duration duration = Duration.between(start, clock.instant());
                                currentCalls.decrementAndGet();
                                if (status.isOk()) {
                                    cache.getTimer(metricPrefix + "Successes")
                                            .update(duration);
                                } else {
                                    cache.getTimer(metricPrefix + "Failures")
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
