package com.avast.metrics.api;

public interface Naming {

    public static Naming defaultNaming() {
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

    public String successTimerName(String base);
    public String failureTimerName(String base);
}
