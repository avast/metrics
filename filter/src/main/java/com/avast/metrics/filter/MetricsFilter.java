package com.avast.metrics.filter;

import com.typesafe.config.Config;

import java.util.List;

/**
 * Abstract metrics filter.
 */
public interface MetricsFilter {
    /**
     * Get effective configuration for a metric according its name.
     */
    boolean isEnabled(String metricName);

    static MetricsFilter newInstance(List<FilterConfig> configuration) {
        return new ConfigurableFilter(configuration);
    }

    static MetricsFilter fromConfig(Config config) {
        return newInstance(new ConfigLoader().load(config));
    }
}
