package com.avast.metrics.dropwizard.formatting.config;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class TimerConfig {
    private List<Double> percentiles;
    private boolean count;
    private boolean min;
    private boolean max;
    private boolean mean;
    private boolean stdDev;
    private boolean oneMinuteRate;
    private boolean fiveMinuteRate;
    private boolean fifteenMinuteRate;

    public List<Double> getPercentiles() {
        return percentiles;
    }

    public void setPercentiles(List<Double> percentiles) {
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

    public boolean isOneMinuteRate() {
        return oneMinuteRate;
    }

    public void setOneMinuteRate(boolean oneMinuteRate) {
        this.oneMinuteRate = oneMinuteRate;
    }

    public boolean isFiveMinuteRate() {
        return fiveMinuteRate;
    }

    public void setFiveMinuteRate(boolean fiveMinuteRate) {
        this.fiveMinuteRate = fiveMinuteRate;
    }

    public boolean isFifteenMinuteRate() {
        return fifteenMinuteRate;
    }

    public void setFifteenMinuteRate(boolean fifteenMinuteRate) {
        this.fifteenMinuteRate = fifteenMinuteRate;
    }
}
