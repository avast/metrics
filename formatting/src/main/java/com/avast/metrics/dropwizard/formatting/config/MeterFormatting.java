package com.avast.metrics.dropwizard.formatting.config;

@SuppressWarnings({"WeakerAccess", "unused"})
public class MeterFormatting {
    private boolean count;
    private boolean mean;
    private boolean oneMinuteRate;
    private boolean fiveMinuteRate;
    private boolean fifteenMinuteRate;

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    public boolean isMean() {
        return mean;
    }

    public void setMean(boolean mean) {
        this.mean = mean;
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
