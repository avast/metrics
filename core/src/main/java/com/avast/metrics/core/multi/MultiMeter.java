package com.avast.metrics.core.multi;

import com.avast.metrics.api.Meter;

import java.util.List;

/**
 * Meter used by {@link MultiMonitor}.
 */
class MultiMeter implements Meter {
    private final List<Meter> meters;

    public MultiMeter(List<Meter> meters) {
        if (meters.size() < 2) {
            throw new IllegalArgumentException("Multi meter from less than 2 meters makes no sense");
        }

        this.meters = meters;
    }

    @Override
    public void mark() {
        meters.forEach(Meter::mark);
    }

    @Override
    public void mark(long n) {
        meters.forEach(m -> m.mark(n));
    }

    @Override
    public long count() {
        return meters.get(0).count();
    }

    @Override
    public String getName() {
        return meters.get(0).getName();
    }

}
