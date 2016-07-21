package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Naming;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ObjectNameFactory;

public class JmxMetricsMonitor extends MetricsMonitor {

    private final JmxReporter reporter;

    public JmxMetricsMonitor(String domain) {
        this(domain, new MetricRegistry(), Naming.defaultNaming());
    }

    public JmxMetricsMonitor(String domain, MetricRegistry metricRegistry, Naming naming) {
        this(TreeObjectNameFactory.getInstance(), domain, metricRegistry, naming);
    }

    public JmxMetricsMonitor(ObjectNameFactory objectNameFactory, String domain, Naming naming) {
        this(objectNameFactory, domain, new MetricRegistry(), naming);
    }

    public JmxMetricsMonitor(ObjectNameFactory objectNameFactory, String domain, MetricRegistry metricRegistry, Naming naming) {
        super(metricRegistry, naming);
        this.reporter = JmxReporter
            .forRegistry(registry)
            .inDomain(domain)
            .createsObjectNamesWith(objectNameFactory)
            .build();
        this.reporter.start();
    }

    private JmxMetricsMonitor(JmxMetricsMonitor original, String... names) {
        super(original, names);
        this.reporter = original.reporter;
    }

    @Override
    public JmxMetricsMonitor named(String name) {
        return new JmxMetricsMonitor(this, name);
    }

    @Override
    public JmxMetricsMonitor named(String name1, String name2, String... restOfNames) {
        return new JmxMetricsMonitor(named(name1).named(name2), restOfNames);
    }

    @Override
    protected String separator() {
        return TreeObjectNameFactory.SEPARATOR;
    }

    @Override
    public void close() {
        LOGGER.debug("Stopping JmxReporter");
        reporter.stop();
        super.close();
    }

}
