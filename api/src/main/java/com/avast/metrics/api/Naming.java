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
        };
    }

    String successTimerName(String base);

    String failureTimerName(String base);
}
