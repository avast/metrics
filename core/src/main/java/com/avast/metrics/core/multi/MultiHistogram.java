package com.avast.metrics.core.multi;

import com.avast.metrics.api.Histogram;

import java.util.List;

/**
 * Histogram used by {@link MultiMonitor}.
 */
class MultiHistogram implements Histogram {
    private final List<Histogram> histograms;

    public MultiHistogram(List<Histogram> histograms) {
        if (histograms.size() < 2) {
            throw new IllegalArgumentException("Multi histogram from less than 2 histograms makes no sense");
        }

        this.histograms = histograms;
    }

    @Override
    public void update(long value) {
        histograms.forEach(h -> h.update(value));
    }

    @Override
    public String getName() {
        return histograms.get(0).getName();
    }

}
