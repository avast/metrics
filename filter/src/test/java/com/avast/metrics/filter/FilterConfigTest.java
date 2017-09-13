package com.avast.metrics.filter;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilterConfigTest {
    @Test
    public void testConstructorWithDefaults() throws Exception {
        assertTrue(new FilterConfig("", true).isEnabled());
        assertFalse(new FilterConfig("", false).isEnabled());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorSampleRateNegative() throws Exception {
        new FilterConfig("", true, -0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorSampleRateTooHigh() throws Exception {
        new FilterConfig("", true, 1.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorSampleRatePercent() throws Exception {
        new FilterConfig("", true, 42);
    }
}
