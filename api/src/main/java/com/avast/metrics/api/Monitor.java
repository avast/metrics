package com.avast.metrics.api;

import java.util.function.Supplier;

public interface Monitor extends AutoCloseable {

    /**
     * Returns a new instance with the given name. This method may be called multiple
     * times - the resulting monitor will remember all its names (basically creating
     * a hierarchy of names).
     *
     * @param name name of the next sub-level
     * @return new monitor derived from this one
     */
    Monitor named(String name);

    /**
     * Returns a new instance with the given names appended.
     *
     * @param name1       name of the next sub-level
     * @param name2       name of the next sub-sub-level
     * @param restOfNames names of the other sub-levels
     * @return new monitor derived from this one
     */
    Monitor named(String name1, String name2, String... restOfNames);

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
