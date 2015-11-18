package com.avast.metrics.api;

import java.util.function.Supplier;

public interface Monitor extends AutoCloseable {

    /**
     * Returns a new instance with the given name. This method may be called multiple
     * times - the resulting monitor will remember all its names (basically creating
     * a hierarchy of names).
     */
    Monitor named(String... names);

    String getName();

    Meter newMeter(String name);

    Counter newCounter(String name);

    Timer newTimer(String name);

    <T> Gauge<T> newGauge(String name, Supplier<T> gauge);

    Histogram newHistogram(String name);

    void remove(Metric metric);

    @Override
    void close();

}
