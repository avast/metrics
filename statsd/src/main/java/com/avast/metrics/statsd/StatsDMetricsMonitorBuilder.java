package com.avast.metrics.statsd;

import com.avast.metrics.api.Naming;
import com.avast.metrics.filter.MetricsFilter;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class StatsDMetricsMonitorBuilder {
		private String host;
		private int port;
		private boolean autoRegisterMetrics = false;
		private String prefix;
		private Naming naming = Naming.defaultNaming();
		private Duration gaugeSendPeriod = getDefaultGaugeSendPeriod();
		private ScheduledExecutorService scheduler = createScheduler();
		private MetricsFilter metricsFilter = MetricsFilter.ALL_ENABLED;

		public StatsDMetricsMonitorBuilder setHost(String host) {
				this.host = host;
				return this;
		}

		public StatsDMetricsMonitorBuilder setPort(int port) {
				this.port = port;
				return this;
		}

		public StatsDMetricsMonitorBuilder setAutoRegisterMetrics(boolean autoRegisterMetrics) {
				this.autoRegisterMetrics = autoRegisterMetrics;
				return this;
		}

		public StatsDMetricsMonitorBuilder setPrefix(String prefix) {
				this.prefix = prefix;
				return this;
		}

		public StatsDMetricsMonitorBuilder setNaming(Naming naming) {
				this.naming = naming;
				return this;
		}

		public StatsDMetricsMonitorBuilder setGaugeSendPeriod(Duration gaugeSendPeriod) {
				this.gaugeSendPeriod = gaugeSendPeriod;
				return this;
		}

		public StatsDMetricsMonitorBuilder setScheduler(ScheduledExecutorService scheduler) {
				this.scheduler = scheduler;
				return this;
		}

		public StatsDMetricsMonitorBuilder setMetricsFilter(MetricsFilter metricsFilter) {
				this.metricsFilter = metricsFilter;
				return this;
		}

		public StatsDMetricsMonitor createStatsDMetricsMonitor() {
				return new StatsDMetricsMonitor(host, port, autoRegisterMetrics, prefix, naming, gaugeSendPeriod, scheduler, metricsFilter);
		}

		public StatsDMetricsMonitor createStatsDMetricsMonitor(StatsDMetricsMonitor monitor, String... newNames) {
			return new StatsDMetricsMonitor(monitor, gaugeSendPeriod, scheduler, newNames);
		}

		public StatsDMetricsMonitor createStatsDMetricsMonitor(StatsDMetricsMonitor monitor, MetricsFilter metricsFilter, String... newNames) {
				return new StatsDMetricsMonitor(monitor, gaugeSendPeriod, scheduler, metricsFilter, newNames);
		}

		private static Duration getDefaultGaugeSendPeriod() {
				return Duration.ofSeconds(1);
		}

		private static ScheduledExecutorService createScheduler() {
				return Executors.newScheduledThreadPool(2);
		}
}