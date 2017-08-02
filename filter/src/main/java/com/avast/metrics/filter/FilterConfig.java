package com.avast.metrics.filter;

/**
 * Configuration for a single filter.
 */
public class FilterConfig {
    private final String metricName;
    private final FilterLevel level;

    public FilterConfig(String metricName, FilterLevel level) {
        this.metricName = metricName;
        this.level = level;
    }

    public FilterConfig withMetricName(String newName) {
        if (metricName.equals(newName)) {
            return this;
        } else {
            return new FilterConfig(newName, level);
        }
    }

    public String getMetricName() {
        return metricName;
    }

    public FilterLevel getLevel() {
        return level;
    }
}
