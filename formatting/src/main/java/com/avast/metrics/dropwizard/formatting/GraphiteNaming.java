package com.avast.metrics.dropwizard.formatting;

import com.avast.metrics.api.Naming;

public class GraphiteNaming implements Naming {
    static final String SEPARATOR_NAME_PARTS = ".";

    @Override
    public String successTimerName(String base) {
        return base + "Successes";
    }

    @Override
    public String failureTimerName(String base) {
        return base + "Failures";
    }

    @Override
    public String countName(String base) {
        return base + SEPARATOR_NAME_PARTS + "count";
    }

    @Override
    public String meanName(String base) {
        return base + SEPARATOR_NAME_PARTS + "mean";
    }

    @Override
    public String oneMinuteRateName(String base) {
        return base + SEPARATOR_NAME_PARTS + "1mRate";
    }

    @Override
    public String fiveMinuteRateName(String base) {
        return base + SEPARATOR_NAME_PARTS + "5mRate";
    }

    @Override
    public String fifteenMinuteRateName(String base) {
        return base + SEPARATOR_NAME_PARTS + "15mRate";
    }

    @Override
    public String minName(String base) {
        return base + SEPARATOR_NAME_PARTS + "min";
    }

    @Override
    public String maxName(String base) {
        return base + SEPARATOR_NAME_PARTS + "max";
    }

    @Override
    public String stdDevName(String base) {
        return base + SEPARATOR_NAME_PARTS + "stdDev";
    }

    @Override
    public String percentileName(String base, int percentile) {
        return base + SEPARATOR_NAME_PARTS + percentile + "Perc";
    }
}
