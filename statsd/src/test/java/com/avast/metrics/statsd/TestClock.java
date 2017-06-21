package com.avast.metrics.statsd;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

public class TestClock extends Clock {

    private final Iterator<Instant> instants;


    public TestClock(Integer... times) {
        this.instants = Arrays.stream(times)
                .map(Instant::ofEpochMilli)
                .collect(Collectors.toList()).iterator();
    }

    @Override
    public ZoneId getZone() {
        return ZoneId.systemDefault();
    }

    @Override
    public Clock withZone(final ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return instants.next();
    }
}
