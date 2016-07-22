package com.avast.metrics.dropwizard;

import com.avast.metrics.api.*;
import com.avast.metrics.test.NoOpMonitor;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ObjectNameFactory;

import java.util.Objects;
import java.util.function.Supplier;


/**
 *
 */
public class JmxMetricsMonitor extends MetricsMonitor {

    private final JmxReporter reporter;
    private final Monitor overrideMonitor;

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
        this.overrideMonitor = overrideMonitor();
    }

    private JmxMetricsMonitor(JmxMetricsMonitor original, String... names) {
        super(original, names);
        this.reporter = original.reporter;
        this.overrideMonitor = overrideMonitor();
    }

    private Monitor overrideMonitor() {
        String prop = System.getProperty("disableMetricsJmx");
        if(prop != null) {
            if(prop.equals("true")) {
                return NoOpMonitor.INSTANCE;
            } else {
                return null;
            }
        } else {
            return null;
        }
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

    @Override
    public Meter newMeter(String name) {
        return overrideMonitor == null ? super.newMeter(name) : overrideMonitor.newMeter(name);
    }

    @Override
    public Counter newCounter(String name) {
        return overrideMonitor == null ? super.newCounter(name) : overrideMonitor.newCounter(name);
    }

    @Override
    public Timer newTimer(String name) {
        return overrideMonitor == null ? super.newTimer(name) : overrideMonitor.newTimer(name);
    }

    @Override
    public TimerPair newTimerPair(String name) {
        return overrideMonitor == null ? super.newTimerPair(name) : overrideMonitor.newTimerPair(name);
    }

    @Override
    public <T> Gauge<T> newGauge(String name, Supplier<T> gauge) {
        return overrideMonitor == null ? super.newGauge(name, gauge) : overrideMonitor.newGauge(name, gauge);
    }

    @Override
    public Histogram newHistogram(String name) {
        return overrideMonitor == null ? super.newHistogram(name) : overrideMonitor.newHistogram(name);

    }
}
