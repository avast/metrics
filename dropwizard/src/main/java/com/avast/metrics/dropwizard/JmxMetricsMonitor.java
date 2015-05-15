package com.avast.metrics.dropwizard;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.ObjectNameFactory;

public class JmxMetricsMonitor extends MetricsMonitor {

    private final JmxReporter reporter;

    public JmxMetricsMonitor(String domain) {
        this(TreeObjectNameFactory.getInstance(), domain);
    }

    public JmxMetricsMonitor(ObjectNameFactory objectNameFactory, String domain) {
        this.reporter = JmxReporter
            .forRegistry(registry)
            .inDomain(domain)
            .createsObjectNamesWith(objectNameFactory)
            .build();
        this.reporter.start();
    }
}
