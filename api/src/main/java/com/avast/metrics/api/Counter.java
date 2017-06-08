package com.avast.metrics.api;

public interface Counter extends Metric {

    void inc();

    void inc(long n);

    void dec();

    void dec(int n);

    /**
     * @return The counter's current value
     */
    long count();

}
