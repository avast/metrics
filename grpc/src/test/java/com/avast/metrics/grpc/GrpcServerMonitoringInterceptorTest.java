package com.avast.metrics.grpc;

import com.avast.metrics.api.Meter;
import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Timer;
import io.grpc.ManagedChannel;
import io.grpc.ServerInterceptors;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class GrpcServerMonitoringInterceptorTest {

    @Test
    public void testOk() throws IOException {
        final String channelName = UUID.randomUUID().toString();

        final Monitor monitor = mock(Monitor.class);
        final Monitor methodMonitor = mock(Monitor.class);
        when(monitor.named("TestApiService_Get")).thenReturn(methodMonitor);

        final Timer timer = mock(Timer.class);
        when(methodMonitor.newTimer("Successes")).thenReturn(timer);
        doNothing().when(timer).update(Matchers.eq(Duration.ofMillis(42)));

        AtomicReference<Supplier<Integer>> currentCallsSupplier = new AtomicReference<>();
        when(methodMonitor.newGauge(eq("Current"), any())).thenAnswer(invocation -> {
            currentCallsSupplier.set(invocation.getArgumentAt(1, Supplier.class));
            return null;
        });

        final Monitor callsMonitor = mock(Monitor.class);
        when(monitor.named("TestApiService_Get", "Calls")).thenReturn(callsMonitor);

        final Meter callsMeter = mock(Meter.class);
        when(callsMonitor.newMeter(eq("count"))).thenReturn(callsMeter);

        final Clock clock = mock(Clock.class);
        when(clock.instant()).thenReturn(
                Instant.ofEpochMilli(0),
                Instant.ofEpochMilli(42)
        );

        final GrpcServerMonitoringInterceptor monitoringInterceptor = new GrpcServerMonitoringInterceptor(monitor, clock);

        InProcessServerBuilder.forName(channelName)
                .directExecutor()
                .addService(ServerInterceptors.intercept(new TestApiServiceGrpc.TestApiServiceImplBase() {
                    @Override
                    public void get(final TestApiOuterClass.TestApi.GetRequest request, final StreamObserver<TestApiOuterClass.TestApi.GetResponse> responseObserver) {
                        responseObserver.onNext(TestApiOuterClass.TestApi.GetResponse.newBuilder().putResults("name", 42).build());
                        responseObserver.onCompleted();
                    }
                }, monitoringInterceptor))
                .build()
                .start();

        final ManagedChannel channel = InProcessChannelBuilder.forName(channelName).directExecutor().build();
        final TestApiServiceGrpc.TestApiServiceBlockingStub stub = TestApiServiceGrpc.newBlockingStub(channel);

        final TestApiOuterClass.TestApi.GetResponse response = stub.get(TestApiOuterClass.TestApi.GetRequest.newBuilder().build());

        assertEquals(TestApiOuterClass.TestApi.GetResponse.newBuilder().putResults("name", 42).build(), response);

        verify(timer, times(1)).update(Matchers.eq(Duration.ofMillis(42)));
        assertEquals(0, currentCallsSupplier.get().get().longValue());
        verify(callsMeter, times(1)).mark();
    }

    @Test
    public void testFailure() throws IOException {
        final String channelName = UUID.randomUUID().toString();

        final Monitor monitor = mock(Monitor.class);
        final Monitor methodMonitor = mock(Monitor.class);
        when(monitor.named("TestApiService_Get")).thenReturn(methodMonitor);

        final Timer timer = mock(Timer.class);
        when(methodMonitor.newTimer("FatalServerFailures")).thenReturn(timer);
        doNothing().when(timer).update(Matchers.eq(Duration.ofMillis(42)));

        AtomicReference<Supplier<Integer>> currentCallsSupplier = new AtomicReference<>();
        when(methodMonitor.newGauge(eq("Current"), any())).thenAnswer(invocation -> {
            currentCallsSupplier.set(invocation.getArgumentAt(1, Supplier.class));
            return null;
        });

        final Monitor callsMonitor = mock(Monitor.class);
        when(monitor.named("TestApiService_Get", "Calls")).thenReturn(callsMonitor);

        final Meter callsMeter = mock(Meter.class);
        when(callsMonitor.newMeter(eq("count"))).thenReturn(callsMeter);

        final Clock clock = mock(Clock.class);
        when(clock.instant()).thenReturn(
                Instant.ofEpochMilli(0),
                Instant.ofEpochMilli(42)
        );

        final GrpcServerMonitoringInterceptor monitoringInterceptor = new GrpcServerMonitoringInterceptor(monitor, clock);

        InProcessServerBuilder.forName(channelName)
                .directExecutor()
                .addService(ServerInterceptors.intercept(new TestApiServiceGrpc.TestApiServiceImplBase() {
                    @Override
                    public void get(final TestApiOuterClass.TestApi.GetRequest request, final StreamObserver<TestApiOuterClass.TestApi.GetResponse> responseObserver) {
                        responseObserver.onError(Status.INTERNAL.asRuntimeException());
                    }
                }, monitoringInterceptor))
                .build()
                .start();

        final ManagedChannel channel = InProcessChannelBuilder.forName(channelName).directExecutor().build();
        final TestApiServiceGrpc.TestApiServiceBlockingStub stub = TestApiServiceGrpc.newBlockingStub(channel);

        try {
            stub.get(TestApiOuterClass.TestApi.GetRequest.newBuilder().build());
            fail("Exception should have been thrown");
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() != Status.INTERNAL.getCode()) fail("Wrong status");
        }

        verify(timer, times(1)).update(Matchers.eq(Duration.ofMillis(42)));
        assertEquals(0, currentCallsSupplier.get().get().longValue());
        verify(callsMeter, times(1)).mark();
    }
}

