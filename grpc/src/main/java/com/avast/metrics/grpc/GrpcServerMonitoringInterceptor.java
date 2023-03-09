package com.avast.metrics.grpc;

import com.avast.metrics.api.Monitor;
import io.grpc.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
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
        MethodDescriptor<ReqT, RespT> method = call.getMethodDescriptor();

        cache.getMeter(method, "Calls", "count").mark();

        final AtomicInteger currentCalls = cache.getGaugedValueByHostname(method, "CurrentByHostname");
        final Instant start = clock.instant();

        currentCalls.incrementAndGet();
        final CurrentCallsReleaser currentCallsReleaser = new CurrentCallsReleaser(currentCalls);

        final ServerCall<ReqT, RespT> newCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(final Status status, final Metadata trailers) {
                final Duration duration = Duration.between(start, clock.instant());
                currentCallsReleaser.release();

                String metricName = "ServerFailures";
                if (status.isOk()) {
                    metricName = "Successes";
                } else if (status.getCode() == Status.Code.RESOURCE_EXHAUSTED) {
                    metricName = "ResourceExhausteds";
                } else if (ErrorCategory.fatal.contains(status.getCode())) {
                    metricName = "FatalServerFailures";
                } else if (ErrorCategory.client.contains(status.getCode())) {
                    metricName = "ClientFailures";
                }
                cache.getTimer(method, metricName).update(duration);

                super.close(status, trailers);
            }
        };

        ServerCall.Listener<ReqT> nextListener;
        try {
            nextListener = next.startCall(newCall, headers);
        } catch (RuntimeException e) {
            currentCallsReleaser.release();
            cache.getMeter(method, "UnhandledExceptionFailures").mark();
            throw e;
        }

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(nextListener) {
            @Override
            public void onMessage(final ReqT message) {
                try {
                    super.onMessage(message);
                } catch (RuntimeException e) {
                    currentCallsReleaser.release();
                    cache.getMeter(method, "UnhandledExceptionFailures").mark();
                    throw e;
                }
            }

            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();}
                catch (RuntimeException e) {
                    currentCallsReleaser.release();
                    cache.getMeter(method, "UnhandledExceptionFailures").mark();
                    throw e;
                }
            }

            @Override
            public void onReady() {
                try {
                    super.onReady();}
                catch (RuntimeException e) {
                    currentCallsReleaser.release();
                    cache.getMeter(method, "UnhandledExceptionFailures").mark();
                    throw e;
                }
            }
        };
    }

    private static class CurrentCallsReleaser {
        private final AtomicInteger currentCalls;
        private final AtomicBoolean acquired = new AtomicBoolean(true);
        CurrentCallsReleaser(AtomicInteger currentCalls) {
            this.currentCalls = currentCalls;
        }

        void release() {
            if(acquired.compareAndSet(true, false)) {
                currentCalls.decrementAndGet();
            }
        }
    }
}
