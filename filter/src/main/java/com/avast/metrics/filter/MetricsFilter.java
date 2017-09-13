package com.avast.metrics.filter;

import com.typesafe.config.Config;

import java.util.List;

/**
 * Abstract metrics filter.
 */
@FunctionalInterface
public interface MetricsFilter {
    String ROOT_FILTER_NAME = "root";

    MetricsFilter ALL_ENABLED = metricName -> FilterConfig.ENABLED;
    MetricsFilter ALL_DISABLED = metricName -> FilterConfig.DISABLED;

    /**
     * Get effective configuration for a metric according to its name.
     *
     * @param metricName metric name in compatible format
     * @return configuration
     */
    FilterConfig getConfig(String metricName);

    default boolean isEnabled(String metricName) {
        return getConfig(metricName).isEnabled();
    }

    static MetricsFilter newInstance(List<FilterConfig> configuration, String nameSeparator) {
        return new ConfigurableFilter(configuration, nameSeparator);
    }

    static MetricsFilter fromConfig(Config config, String nameSeparator) {
        return newInstance(new ConfigLoader(nameSeparator).load(config), nameSeparator);
    }
}
