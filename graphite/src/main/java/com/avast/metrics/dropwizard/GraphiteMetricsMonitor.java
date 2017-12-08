package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Naming;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.TimeUnit;


public class GraphiteMetricsMonitor extends MetricsMonitor {

    public static final Duration DEFAULT_PERIOD = Duration.ofSeconds(5);
    public static final int DEFAULT_GRAPHITE_PORT = 2003;

    private final GraphiteReporter reporter;

    /**
     * Creates new GraphiteMetricsMonitor using given host and {@code DEFAULT_GRAPHITE_PORT}.
     *
     * @param graphiteHost Graphite server host
     * @param domain       Domain for publishing
     */
    public GraphiteMetricsMonitor(String graphiteHost, String domain) {
        this(new InetSocketAddress(graphiteHost, DEFAULT_GRAPHITE_PORT), domain, new MetricRegistry(), Naming.defaultNaming(), DEFAULT_PERIOD);
    }

    public GraphiteMetricsMonitor(InetSocketAddress graphiteServer, String domain) {
        this(graphiteServer, domain, new MetricRegistry(), Naming.defaultNaming(), DEFAULT_PERIOD);
    }

    public GraphiteMetricsMonitor(InetSocketAddress graphiteServer, String domain, Duration publishPeriod) {
        this(graphiteServer, domain, new MetricRegistry(), Naming.defaultNaming(), publishPeriod);
    }

    public GraphiteMetricsMonitor(InetSocketAddress graphiteServer, String domain, MetricRegistry metricRegistry, Naming naming, Duration publishPeriod) {
        super(metricRegistry, naming);
        final Graphite graphite = new Graphite(graphiteServer);

        this.reporter = GraphiteReporter
                .forRegistry(registry)
                .prefixedWith(domain)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(graphite);

        this.reporter.start(publishPeriod.toMillis(), TimeUnit.MILLISECONDS);
    }

    private GraphiteMetricsMonitor(GraphiteMetricsMonitor original, String... names) {
        super(original, names);
        this.reporter = original.reporter;
    }


    @Override
    public GraphiteMetricsMonitor named(String name) {
        return new GraphiteMetricsMonitor(this, name);
    }

    @Override
    public GraphiteMetricsMonitor named(String name1, String name2, String... restOfNames) {
        return new GraphiteMetricsMonitor(named(name1).named(name2), restOfNames);
    }

    @Override
    public void close() {
        LOGGER.debug("Stopping GraphiteReporter");
        if (reporter != null) {
            reporter.stop();
        }
        super.close();
    }

}
