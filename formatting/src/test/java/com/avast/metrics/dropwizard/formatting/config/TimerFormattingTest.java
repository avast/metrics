package com.avast.metrics.dropwizard.formatting.config;

import org.junit.Test;

import java.util.Collections;

public class TimerFormattingTest {
    @Test
    public void testPercentileValid() throws Exception {
        new TimerFormatting().setPercentiles(Collections.singletonList(0.5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPercentileBelowValid() throws Exception {
        new TimerFormatting().setPercentiles(Collections.singletonList(-0.1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPercentileAboveValid() throws Exception {
        new TimerFormatting().setPercentiles(Collections.singletonList(1.1));
    }
}
