package com.avast.metrics.filter;

/**
 * Configuration for a single filter.
 */
public class FilterConfig {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterConfig that = (FilterConfig) o;
        return enabled == that.enabled && metricName.equals(that.metricName);
    }

    @Override
    public int hashCode() {
        int result = metricName.hashCode();
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return metricName + " - " + (enabled ? "enabled" : "disabled");
    }
}
