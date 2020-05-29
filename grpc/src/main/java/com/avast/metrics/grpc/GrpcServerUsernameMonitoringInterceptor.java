package com.avast.metrics.grpc;

import com.avast.metrics.api.Monitor;
import com.google.common.base.Strings;
import io.grpc.*;

import java.util.function.Supplier;

public class GrpcServerUsernameMonitoringInterceptor implements ServerInterceptor {
    private final MetricsCache cache;
    private final Supplier<String> usernameSupplier;

    /*
     * @param usernameSupplier Callback that is called from the right gRPC Context so can access its values.
     */
    public GrpcServerUsernameMonitoringInterceptor(final Monitor monitor, Supplier<String> usernameSupplier) {
        cache = new MetricsCache(monitor);
        this.usernameSupplier = usernameSupplier;
    }

    protected String sanitizeUsername(String un) {
        if (Strings.isNullOrEmpty(un)) return "anonymous";
        return un.replace('.', '_');
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(next.startCall(call, headers)) {
            @Override
            public void onComplete() {
                cache.getMeter(call.getMethodDescriptor(), sanitizeUsername(usernameSupplier.get())).mark();
                super.onComplete();
            }
        };
    }
}
