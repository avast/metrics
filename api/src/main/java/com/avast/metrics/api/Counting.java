package com.avast.metrics.api;

public interface Counting extends Metric {
    /**
     * Get counted value. Delta in returned number must correspond to the number of events that were counted.
     *
     * @return counted value
     */
    long count();
}
