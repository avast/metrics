package com.avast.metrics.dropwizard.formatting.fields;

import org.junit.Test;

import java.util.Collections;

public class HistogramFormattingTest {
    @Test
    public void testPercentileValid() throws Exception {
        new HistogramFormatting().setPercentiles(Collections.singletonList(0.5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPercentileBelowValid() throws Exception {
        new HistogramFormatting().setPercentiles(Collections.singletonList(-0.1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPercentileAboveValid() throws Exception {
        new HistogramFormatting().setPercentiles(Collections.singletonList(1.1));
    }
}
