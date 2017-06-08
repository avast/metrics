package com.avast.metrics.api;

public interface Meter extends Metric {

    void mark();

    void mark(long n);

    /**
     * @return Total number of events in this meter.
     */
    long count();

}
