package com.avast.metrics.dropwizard;

public class DuplicateMetricNameException extends RuntimeException {

    public DuplicateMetricNameException() {
    }

    public DuplicateMetricNameException(String message) {
        super(message);
    }

    public DuplicateMetricNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateMetricNameException(Throwable cause) {
        super(cause);
    }

    protected DuplicateMetricNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
