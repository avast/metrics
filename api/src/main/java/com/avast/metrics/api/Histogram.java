package com.avast.metrics.api;

public interface Histogram extends Metric {

    void update(long value);

}
