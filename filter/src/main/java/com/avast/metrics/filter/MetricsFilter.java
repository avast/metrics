package com.avast.metrics.filter;

/**
 * Abstract metrics filter.
 */
public interface MetricsFilter {
    /**
     * Get effective configuration for a metric according its name.
     */
    FilterConfig getConfig(String metricName);
}
