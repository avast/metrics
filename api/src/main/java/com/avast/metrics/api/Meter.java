package com.avast.metrics.api;

public interface Meter extends Counting {

    void mark();

    void mark(long n);

}
