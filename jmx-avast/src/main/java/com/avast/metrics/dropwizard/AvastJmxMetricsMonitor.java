package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Naming;
import com.codahale.metrics.MetricRegistry;

@SuppressWarnings("WeakerAccess")
public class AvastJmxMetricsMonitor extends JmxMetricsMonitor {

    public AvastJmxMetricsMonitor(String domain) {
        this(domain, new MetricRegistry(), Naming.defaultNaming());
    }

    public AvastJmxMetricsMonitor(String domain, MetricRegistry metricRegistry, Naming naming) {
        super(AvastTreeObjectNameFactory.getInstance(), domain, metricRegistry, naming);
    }

    private AvastJmxMetricsMonitor(AvastJmxMetricsMonitor original, String... names) {
        super(original, names);
    }


    @Override
    public AvastJmxMetricsMonitor named(String name) {
        return new AvastJmxMetricsMonitor(this, name);
    }

    @Override
    public AvastJmxMetricsMonitor named(String name1, String name2, String... restOfNames) {
        return new AvastJmxMetricsMonitor(named(name1).named(name2), restOfNames);
    }

    @Override
    protected String separator() {
        return AvastTreeObjectNameFactory.SEPARATOR;
    }

}
