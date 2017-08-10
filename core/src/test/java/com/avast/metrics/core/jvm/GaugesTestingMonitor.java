package com.avast.metrics.core.jvm;

import com.avast.metrics.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class GaugesTestingMonitor implements Monitor {
    private final String monitorName;

    /**
     * All registered gauges in creation order.
     */
    private final List<Gauge<?>> gauges;

    GaugesTestingMonitor() {
        this("", new ArrayList<>());
    }

    private GaugesTestingMonitor(String monitorName, List<Gauge<?>> gauges) {
        this.monitorName = monitorName;
        this.gauges = gauges;
    }

    @Override
    public Monitor named(String name) {
        return new GaugesTestingMonitor((monitorName.isEmpty() ? "" : monitorName + ".") + name, this.gauges);
    }

    @Override
    public Monitor named(String name1, String name2, String... restOfNames) {
        String base = (monitorName.isEmpty() ? "" : monitorName + ".") + name1 + "." + name2;

        if (restOfNames.length > 0) {
            String rest = Arrays.stream(restOfNames).collect(Collectors.joining("."));
            return new GaugesTestingMonitor(base + "." + rest, this.gauges);
        } else {
            return new GaugesTestingMonitor(base, this.gauges);
        }
    }

    @Override
    public String getName() {
        return monitorName;
    }

    @Override
    public Meter newMeter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Counter newCounter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Timer newTimer(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TimerPair newTimerPair(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Gauge<T> newGauge(String name, Supplier<T> gauge) {
        return newGauge(name, false, gauge);
    }

    @Override
    public <T> Gauge<T> newGauge(String name, boolean replaceExisting, Supplier<T> gauge) {
        Gauge<T> g = new Gauge<T>() {
            @Override
            public T getValue() {
                return gauge.get();
            }

            @Override
            public String getName() {
                return monitorName + "." + name;
            }
        };

        gauges.add(g);
        return g;
    }

    List<Gauge<?>> getGauges() {
        return gauges;
    }

    @Override
    public Histogram newHistogram(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Metric metric) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {

    }
}
