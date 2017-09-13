package com.avast.metrics.filter;

/**
 * Configuration for a single filter.
 */
public class FilterConfig {
    static final FilterConfig ENABLED = new FilterConfig("", true);
    static final FilterConfig DISABLED = new FilterConfig("", false);

    private final String metricName;
    private final boolean enabled;

    /**
     * Range 0 - 1.
     */
    private final double sampleRate;

    public FilterConfig(String metricName, boolean enabled, double sampleRate) {
        this.metricName = metricName;
        this.enabled = enabled;
        this.sampleRate = sampleRate;

        if (sampleRate < 0.0 || sampleRate > 1.0) {
            throw new IllegalArgumentException("Sample rate not in range 0.0 - 1.0: " + sampleRate);
        }
    }

    public FilterConfig(String metricName, boolean enabled) {
        this(metricName, enabled, enabledToSampleRate(enabled));
    }

    public FilterConfig withMetricName(String newName) {
        if (metricName.equals(newName)) {
            return this;
        } else {
            return new FilterConfig(newName, enabled, sampleRate);
        }
    }

    public static double enabledToSampleRate(boolean enabled) {
        return enabled ? 1.0 : 0.0;
    }

    public String getMetricName() {
        return metricName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterConfig that = (FilterConfig) o;

        if (enabled != that.enabled) return false;
        if (Double.compare(that.sampleRate, sampleRate) != 0) return false;
        return metricName.equals(that.metricName);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = metricName.hashCode();
        result = 31 * result + (enabled ? 1 : 0);
        temp = Double.doubleToLongBits(sampleRate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return metricName + " - " + (enabled ? "enabled" : "disabled") + "/" + sampleRate;
    }
}
