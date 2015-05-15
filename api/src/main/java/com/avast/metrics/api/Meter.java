package com.avast.metrics.api;

public interface Meter {

    void mark();

    void mark(long n);

    long count();

}
