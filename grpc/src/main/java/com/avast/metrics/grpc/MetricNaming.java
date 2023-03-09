package com.avast.metrics.grpc;


import io.grpc.MethodDescriptor;

import java.net.InetAddress;

public class MetricNaming {
    private MetricNaming() {}

    public static <ReqT, RespT>String getMethodMonitorName(MethodDescriptor<ReqT, RespT> methodDescriptor) {
        return methodDescriptor.getFullMethodName().replace('/', '_');
    }

    public static String getHostname() {
        return hostname;
    }

    // not threadsafe, indended for Tests only
    public static void setHostname(String hostname) {
        MetricNaming.hostname = hostname;
    }

    private static String hostname = retrieveHostname();

    private static String retrieveHostname() {
        String fromEnv = System.getenv("HOSTNAME");
        if(fromEnv != null && !fromEnv.equals("")) {
            return fromEnv;
        }

        try {
            String fromInet = InetAddress.getLocalHost().getHostName();
            if(fromInet != null && !fromInet.equals("")) {
                return fromInet;
            }
        } catch(Exception ignored) {

        }
        return "Unknown";
    }
}
