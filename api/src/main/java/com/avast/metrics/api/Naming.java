package com.avast.metrics.api;

public interface Naming {
    static Naming defaultNaming() {
        return new Naming() {
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
                return base + "Count";
            }

            @Override
            public String meanName(String base) {
                return base + "Mean";
            }

            @Override
            public String oneMinuteRateName(String base) {
                return base + "OneMinuteRate";
            }

            @Override
            public String fiveMinuteRateName(String base) {
                return base + "FiveMinuteRate";
            }

            @Override
            public String fifteenMinuteRateName(String base) {
                return base + "FifteenMinuteRate";
            }

            @Override
            public String minName(String base) {
                return base + "Min";
            }

            @Override
            public String maxName(String base) {
                return base + "Max";
            }

            @Override
            public String stdDevName(String base) {
                return base + "StdDev";
            }

            @Override
            public String percentileName(String base, int percentile) {
                return base + percentile + "thPercentile";
            }
        };
    }

    String successTimerName(String base);

    String failureTimerName(String base);

    String countName(String base);

    String meanName(String base);

    String oneMinuteRateName(String base);

    String fiveMinuteRateName(String base);

    String fifteenMinuteRateName(String base);

    String minName(String base);

    String maxName(String base);

    String stdDevName(String base);

    String percentileName(String base, int percentile);
}
