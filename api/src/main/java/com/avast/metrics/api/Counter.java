package com.avast.metrics.api;

public interface Counter {

    void inc();

    void inc(long n);

    void dec();

    void dec(int n);

    long count();

}
