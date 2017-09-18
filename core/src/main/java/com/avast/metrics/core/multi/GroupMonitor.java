package com.avast.metrics.core.multi;

import com.avast.metrics.TimerPairImpl;
import com.avast.metrics.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Wrapper around a group of monitors.
 */
public class GroupMonitor implements Monitor {

    private final List<Monitor> monitors;
    private final Naming naming;

    /**
     * Factory method.
     *
     * @param monitors   list of monitors
     * @return multiple  monitor containing all passed monitors
     */
    public static Monitor of(Monitor... monitors) {
        return new GroupMonitor(Arrays.asList(monitors),Naming.defaultNaming());
    }

    /**
     * Factory method.
     *
     * @param monitors   list of monitors
     * @param naming     naming conventions for TimerPairs
     * @return multiple  monitor containing all passed monitors
     */
    public static Monitor of(Naming naming, Monitor... monitors) {
        return new GroupMonitor(Arrays.asList(monitors), naming);
    }

    private GroupMonitor(List<Monitor> monitors, Naming naming) {
        if (monitors.size() == 0) {
            throw new IllegalArgumentException("Group monitor from requires at least single instance of Monitor.");
        }
        this.monitors = monitors;
        this.naming = naming;
    }

    /**
     * {@inheritDoc}
     *
     * @param name name of the next sub-level
     * @return new group monitor with name applied to all instances
     */
    @Override
    public Monitor named(String name) {
        List<Monitor> newMonitors = monitors.stream()
                .map(monitor -> monitor.named(name))
                .collect(Collectors.toList());
        return new GroupMonitor(newMonitors, naming);
    }

    /**
     * {@inheritDoc}
     *
     * @param name1       name of the next sub-level
     * @param name2       name of the next sub-level
     * @param restOfNames name of the next sub-level
     * @return new group monitor with name applied to all instances
     */
    @Override
    public Monitor named(String name1, String name2, String... restOfNames) {
        List<Monitor> newMonitors = monitors.stream()
                .map(monitor -> monitor.named(name1, name2, restOfNames))
                .collect(Collectors.toList());
        return new GroupMonitor(newMonitors, naming);
    }

    @Override
    public String getName() {
        return monitors.get(0).getName();
    }

    @Override
    public Meter newMeter(String name) {
        List<Meter> meters = monitors.stream()
                .map(m -> m.newMeter(name))
                .collect(Collectors.toList());

        return new MultiMeter(meters);
    }

    @Override
    public Counter newCounter(String name) {
        List<Counter> counters = monitors.stream()
                .map(m -> m.newCounter(name))
                .collect(Collectors.toList());

        return new MultiCounter(counters);
    }

    @Override
    public Timer newTimer(String name) {
        List<Timer> timers = monitors.stream()
                .map(m -> m.newTimer(name))
                .collect(Collectors.toList());

        return new MultiTimer(timers);
    }

    @Override
    public TimerPair newTimerPair(String name) {
        return new TimerPairImpl(
                newTimer(naming.successTimerName(name)),
                newTimer(naming.failureTimerName(name))
        );
    }

    @Override
    public <T> Gauge<T> newGauge(String name, Supplier<T> gauge) {
        return newGauge(name, false, gauge);
    }

    @Override
    public <T> Gauge<T> newGauge(String name, boolean replaceExisting, Supplier<T> gauge) {
        List<Gauge<T>> gauges = monitors.stream()
                .map(monitor -> monitor.newGauge(name, replaceExisting, gauge))
                .collect(Collectors.toList());
        return gauges.get(0);
    }

    @Override
    public Histogram newHistogram(String name) {
        List<Histogram> histograms = monitors.stream()
                .map(m -> m.newHistogram(name))
                .collect(Collectors.toList());

        return new MultiHistogram(histograms);
    }

    @Override
    public void remove(Metric metric) {
        monitors.forEach(monitor -> monitor.remove(metric));
    }

    @Override
    public void close() {
        monitors.forEach(Monitor::close);
    }

}
