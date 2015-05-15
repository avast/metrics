package com.avast.metrics.api;

import java.util.function.Supplier;

public interface Monitor {

    Monitor named(String name);

    String getName();

    Meter newMeter(String name);

    Counter newCounter(String name);

    Timer newTimer(String name);

    <T> Gauge<T> newGauge(String name, Supplier<T> gauge);

    Histogram newHistogram(String name);

}
