package com.avast.metrics.statsd;

import com.avast.metrics.api.Naming;
import com.avast.metrics.filter.MetricsFilter;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

public class StatsDMetricsMonitorBuilder {
    private final String host;
    private final int port;
    private final String prefix;
    private final ScheduledExecutorService scheduler;
    private boolean autoRegisterMetrics = false;
    private Naming naming = Naming.defaultNaming();
    private Duration gaugeSendPeriod = Duration.ofSeconds(1);
    private MetricsFilter metricsFilter = MetricsFilter.ALL_ENABLED;

    public StatsDMetricsMonitorBuilder(String host, int port, String prefix, final ScheduledExecutorService scheduler) {
        this.host = host;
        this.port = port;
        this.prefix = prefix;
        this.scheduler = scheduler;
    }

    public StatsDMetricsMonitorBuilder withAutoRegisterMetrics(boolean autoRegisterMetrics) {
        this.autoRegisterMetrics = autoRegisterMetrics;
        return this;
    }

    public StatsDMetricsMonitorBuilder withNaming(Naming naming) {
        this.naming = naming;
        return this;
    }

    public StatsDMetricsMonitorBuilder withGaugeSendPeriod(Duration gaugeSendPeriod) {
        this.gaugeSendPeriod = gaugeSendPeriod;
        return this;
    }

    public StatsDMetricsMonitorBuilder withMetricsFilter(MetricsFilter metricsFilter) {
        this.metricsFilter = metricsFilter;
        return this;
    }

    public StatsDMetricsMonitor build() {
        return new StatsDMetricsMonitor(host, port, autoRegisterMetrics, prefix, naming, gaugeSendPeriod, scheduler, metricsFilter);
    }
}