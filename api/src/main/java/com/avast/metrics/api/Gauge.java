package com.avast.metrics.api;

public interface Gauge<T> extends Metric {

    T getValue();

}
