package com.avast.metrics.core.multi;

import com.avast.metrics.api.Counter;

import java.util.List;

/**
 * Counter used by {@link MultiMonitor}.
 */
class MultiCounter implements Counter {
    private final List<Counter> counters;

    public MultiCounter(List<Counter> counters) {
        if (counters.size() < 2) {
            throw new IllegalArgumentException("Multi counter from less than 2 counters makes no sense");
        }

        this.counters = counters;
    }

    @Override
    public void inc() {
        counters.forEach(Counter::inc);
    }

    @Override
    public void inc(long n) {
        counters.forEach(c -> c.inc(n));
    }

    @Override
    public void dec() {
        counters.forEach(Counter::dec);
    }

    @Override
    public void dec(int n) {
        counters.forEach(c -> c.dec(n));
    }

    @Override
    public long count() {
        return counters.get(0).count();
    }

    @Override
    public String getName() {
        return counters.get(0).getName();
    }
}
