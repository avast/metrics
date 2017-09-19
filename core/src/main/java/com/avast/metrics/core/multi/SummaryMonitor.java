package com.avast.metrics.core.multi;

import com.avast.metrics.TimerPairImpl;
import com.avast.metrics.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Wrapper around multiple monitors that adds possibility of per instance and summary metrics stored in a single object
 * and updated by a single call instead of two separate ones.
 * <p>
 * The first of the wrapped monitors is the main monitor instance, it should be the most concrete one. The second
 * is typically a summary per all instances. Only two wrapped monitors are expected at the moment but the internal
 * implementation is generic.
 * <p>
 * <h3>Limitations</h3>
 * <p>
 * The updates are applied to all wrapped monitors the same way, but queries for names and values will always operate only
 * on the first one which represents the monitored instance. Note it is impossible to return two ints at place of one int,
 * adding them together would typically produce a value that doesn't make any sense at all.
 * <p>
 * {@link #named(String)} creates a new summary monitor with the name applied only to the instance monitor, the summary
 * one is untouched and only copied. This allows to dynamically create new sub-monitors while summary is preserved.
 * <p>
 * {@link #remove(Metric)} removes the metric only from the first wrapped monitor. The summary monitor is shared by
 * multiple instances so the remove might cause some unexpected problems in such case.
 * <p>
 * {@link #newGauge(String, Supplier)} is also registered only to the first wrapped monitor. There would be JMX conflicts
 * with the summary monitor shared by multiple instances.
 * <p>
 * <h3>Typical usage</h3>
 * <p>
 * <pre><code>
 *     Monitor summaryMonitor = monitor.named("path");
 *     Monitor instanceMonitor = summaryMonitor.named("instance");
 *
 *     Monitor monitor = SummaryMonitor.of(instanceMonitor, summaryMonitor);
 *
 *     this.errors = monitor.newMeter("errors");
 *     this.requests = monitor.newMeter("requests");
 * </code></pre>
 * <p>
 * <pre><code>
 *     Monitor summaryMonitor = monitor.named("path");
 *     Monitor monitor = SummaryMonitor.of(summaryMonitor, summaryMonitor);
 *     ConcurrentMap&lt;String, Meter&gt; requests = new ConcurrentHashMap&lt;&gt;()
 *
 *     // Dynamically create a new meter if not present in the cache and increment its value
 *     requests.computeIfAbsent(topic, t -&gt; monitor.named(t).newMeter("requests")).mark();
 * </code></pre>
 */
public class SummaryMonitor implements Monitor {

    private final List<Monitor> monitors;
    private final Naming naming;

    /**
     * Factory method. Read {@link SummaryMonitor} limitations!
     *
     * @param instanceMonitor non-shared main monitor for data from a single instance
     * @param summaryMonitor  shared summary monitor counting sums per all instances
     * @return summary monitor containing all passed monitors
     */
    public static Monitor of(Monitor instanceMonitor, Monitor summaryMonitor) {
        List<Monitor> allMonitors = new ArrayList<>(2);
        allMonitors.add(instanceMonitor);
        allMonitors.add(summaryMonitor);

        return new SummaryMonitor(allMonitors, Naming.defaultNaming());
    }

    /**
     * Factory method. Read {@link SummaryMonitor} limitations!
     *
     * @param instanceMonitor non-shared main monitor for data from a single instance
     * @param summaryMonitor  shared summary monitor counting sums per all instances
     * @param naming          naming conventions for TimerPair
     * @return summary monitor containing all passed monitors
     */

    public static Monitor of(Monitor instanceMonitor, Monitor summaryMonitor, Naming naming) {
        List<Monitor> allMonitors = new ArrayList<>(2);
        allMonitors.add(instanceMonitor);
        allMonitors.add(summaryMonitor);

        return new SummaryMonitor(allMonitors, naming);
    }

    SummaryMonitor(List<Monitor> monitors, Naming naming) {
        if (monitors.size() < 2) {
            throw new IllegalArgumentException("Summary monitor from less than 2 monitors makes no sense");
        }

        this.monitors = monitors;
        this.naming = naming;
    }

    /**
     * {@inheritDoc}
     *
     * @param name name of the next sub-level
     * @return new summary monitor with name applied only on the instance one, the summary one is left untouched
     */
    @Override
    public Monitor named(String name) {
        return SummaryMonitor.of(monitors.get(0).named(name), monitors.get(1));
    }

    /**
     * {@inheritDoc}
     *
     * @param name1       name of the next sub-level
     * @param name2       name of the next sub-level
     * @param restOfNames name of the next sub-level
     * @return new summary monitor with names applied only on the instance one, the summary one is left untouched
     */
    @Override
    public Monitor named(String name1, String name2, String... restOfNames) {
        return SummaryMonitor.of(monitors.get(0).named(name1, name2, restOfNames), monitors.get(1));
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
        return newMultiTimer(name);
    }

    private MultiTimer newMultiTimer(String name) {
        List<Timer> timers = monitors.stream()
                .map(m -> m.newTimer(name))
                .collect(Collectors.toList());

        return new MultiTimer(timers);
    }


    /**
     * TimerPair multi monitor. Uses instanceMonitor (first parameter of the factory method) to create failure timer.
     *
     */
    @Override
    public TimerPair newTimerPair(String name) {
        return new TimerPairImpl(
                newMultiTimer(name),
                monitors.get(0).newTimer(naming.successTimerName(name))
        );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Register the gauge only to the instance monitor. There would be JMX conflicts with the summary monitor
     * which is typically shared by multiple instances.
     *
     * @param name  gauge name
     * @param gauge method to compute the gauge value
     * @param <T>   type of computed value
     * @return new gauge
     */
    @Override
    public <T> Gauge<T> newGauge(String name, Supplier<T> gauge) {
        return newGauge(name, false, gauge);
    }

    @Override
    public <T> Gauge<T> newGauge(String name, boolean replaceExisting, Supplier<T> gauge) {
        return monitors.get(0).newGauge(name, replaceExisting, gauge);
    }

    @Override
    public Histogram newHistogram(String name) {
        List<Histogram> histograms = monitors.stream()
                .map(m -> m.newHistogram(name))
                .collect(Collectors.toList());

        return new MultiHistogram(histograms);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Remove the metric only from the instance monitor. There would be JMX conflicts with the summary monitor
     * which is typically shared by multiple instances.
     *
     * @param metric metric to unregister
     */
    @Override
    public void remove(Metric metric) {
        monitors.get(0).remove(metric);
    }

    @Override
    public void close() {
        monitors.forEach(Monitor::close);
    }

}
