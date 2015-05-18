package com.avast.metrics.dropwizard;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.ObjectNameFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    protected String constructMetricName(Optional<String> finalName) {
        List<String> copy = new ArrayList<>(names);
        finalName.ifPresent(copy::add);
        return copy.stream().collect(Collectors.joining(TreeObjectNameFactory.SEPARATOR));
    }
}
