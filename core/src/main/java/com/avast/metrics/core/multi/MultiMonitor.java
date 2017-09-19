package com.avast.metrics.core.multi;

import com.avast.metrics.api.Monitor;
import com.avast.metrics.api.Naming;

import java.util.ArrayList;
import java.util.List;

/**
 * Use SummaryMonitor instead.
 */
@Deprecated()
public class MultiMonitor extends SummaryMonitor {
    /**
     * Factory method. Read {@link MultiMonitor} limitations!
     *
     * @param instanceMonitor non-shared main monitor for data from a single instance
     * @param summaryMonitor  shared summary monitor counting sums per all instances
     * @return summary monitor containing all passed monitors
     */
    public static Monitor of(Monitor instanceMonitor, Monitor summaryMonitor) {
        List<Monitor> allMonitors = new ArrayList<>(2);
        allMonitors.add(instanceMonitor);
        allMonitors.add(summaryMonitor);

        return new MultiMonitor(allMonitors, Naming.defaultNaming());
    }

    /**
     * Factory method. Read {@link MultiMonitor} limitations!
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

        return new MultiMonitor(allMonitors, naming);
    }

    private MultiMonitor(List<Monitor> monitors, Naming naming) {
        super(monitors, naming);
    }
}
