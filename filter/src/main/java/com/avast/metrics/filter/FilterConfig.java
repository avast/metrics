package com.avast.metrics.filter;

/**
 * Configuration for a single filter.
 */
class FilterConfig {
    private final String metricName;
    private final boolean enabled;

    public FilterConfig(String metricName, boolean enabled) {
        this.metricName = metricName;
        this.enabled = enabled;
    }

    public FilterConfig withMetricName(String newName) {
        if (metricName.equals(newName)) {
            return this;
        } else {
            return new FilterConfig(newName, enabled);
        }
    }

    public String getMetricName() {
        return metricName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return metricName + " - " + (enabled ? "enabled" : "disabled");
    }
}
