package com.avast.metrics.dropwizard.formatting.fields;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class HistogramFormatting {
    private List<Double> percentiles;
    private boolean count;
    private boolean min;
    private boolean max;
    private boolean mean;
    private boolean stdDev;

    public List<Double> getPercentiles() {
        return percentiles;
    }

    public void setPercentiles(List<Double> percentiles) {
        percentiles.forEach(percentile -> {
            if (percentile < 0.0 || percentile > 1.0) {
                throw new IllegalArgumentException("Range for percentiles is 0 - 1 inclusively: " + percentile);
            }
        });

        this.percentiles = percentiles;
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    public boolean isMin() {
        return min;
    }

    public void setMin(boolean min) {
        this.min = min;
    }

    public boolean isMax() {
        return max;
    }

    public void setMax(boolean max) {
        this.max = max;
    }

    public boolean isMean() {
        return mean;
    }

    public void setMean(boolean mean) {
        this.mean = mean;
    }

    public boolean isStdDev() {
        return stdDev;
    }

    public void setStdDev(boolean stdDev) {
        this.stdDev = stdDev;
    }
}
