package com.avast.metrics.grpc;

import io.grpc.stub.ClientCalls;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.9.0)",
    comments = "Source: test_api.proto")
public final class TestApiServiceGrpc {

  private TestApiServiceGrpc() {}

  public static final String SERVICE_NAME = "TestApiService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @Deprecated // Use {@link #getGetMethod()} instead.
  public static final io.grpc.MethodDescriptor<com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest,
      com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse> METHOD_GET = getGetMethod();

  private static volatile io.grpc.MethodDescriptor<com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest,
      com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse> getGetMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest,
      com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse> getGetMethod() {
    io.grpc.MethodDescriptor<com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest, com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse> getGetMethod;
    if ((getGetMethod = TestApiServiceGrpc.getGetMethod) == null) {
      synchronized (TestApiServiceGrpc.class) {
        if ((getGetMethod = TestApiServiceGrpc.getGetMethod) == null) {
          TestApiServiceGrpc.getGetMethod = getGetMethod =
              io.grpc.MethodDescriptor.<com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest, com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "TestApiService", "Get"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TestApiServiceMethodDescriptorSupplier("Get"))
                  .build();
          }
        }
     }
     return getGetMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TestApiServiceStub newStub(io.grpc.Channel channel) {
    return new TestApiServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TestApiServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new TestApiServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TestApiServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new TestApiServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class TestApiServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void get(com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest request,
        io.grpc.stub.StreamObserver<com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest,
                com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse>(
                  this, METHODID_GET)))
          .build();
    }
  }

  /**
   */
  public static final class TestApiServiceStub extends io.grpc.stub.AbstractStub<TestApiServiceStub> {
    private TestApiServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TestApiServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TestApiServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TestApiServiceStub(channel, callOptions);
    }

    /**
     */
    public void get(com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest request,
        io.grpc.stub.StreamObserver<com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse> responseObserver) {
      ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TestApiServiceBlockingStub extends io.grpc.stub.AbstractStub<TestApiServiceBlockingStub> {
    private TestApiServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TestApiServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TestApiServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TestApiServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse get(com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TestApiServiceFutureStub extends io.grpc.stub.AbstractStub<TestApiServiceFutureStub> {
    private TestApiServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TestApiServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TestApiServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TestApiServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse> get(
        com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TestApiServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TestApiServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET:
          serviceImpl.get((com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetRequest) request,
              (io.grpc.stub.StreamObserver<com.avast.metrics.grpc.TestApiOuterClass.TestApi.GetResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class TestApiServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TestApiServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.avast.metrics.grpc.TestApiOuterClass.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TestApiService");
    }
  }

  private static final class TestApiServiceFileDescriptorSupplier
      extends TestApiServiceBaseDescriptorSupplier {
    TestApiServiceFileDescriptorSupplier() {}
  }

  private static final class TestApiServiceMethodDescriptorSupplier
      extends TestApiServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TestApiServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TestApiServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TestApiServiceFileDescriptorSupplier())
              .addMethod(getGetMethod())
              .build();
        }
      }
    }
    return result;
  }
}
