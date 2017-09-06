package com.avast.metrics.filter;

import com.typesafe.config.Config;

import java.util.List;

/**
 * Abstract metrics filter.
 */
@FunctionalInterface
public interface MetricsFilter {
    String ROOT_FILTER_NAME = "root";

    MetricsFilter ALL_ENABLED = metricName -> true;
    MetricsFilter ALL_DISABLED = metricName -> false;

    /**
     * Get effective configuration for a metric according to its name.
     *
     * @param metricName metric name in compatible format
     * @return enabled/disabled
     */
    boolean isEnabled(String metricName);

    static MetricsFilter newInstance(List<FilterConfig> configuration, String nameSeparator) {
        return new ConfigurableFilter(configuration, nameSeparator);
    }

    static MetricsFilter fromConfig(Config config, String nameSeparator) {
        return newInstance(new ConfigLoader(nameSeparator).load(config), nameSeparator);
    }
}
