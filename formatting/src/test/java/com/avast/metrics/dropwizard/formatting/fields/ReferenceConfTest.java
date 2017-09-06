package com.avast.metrics.dropwizard.formatting.fields;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ReferenceConfTest {
    @Test
    public void testDefaultsLoadable() throws Exception {
        Config config = ConfigFactory.load().getConfig(FieldsFormatting.SECTION_DEFAULTS);

        assertTrue(config.getBoolean("counter.count"));

        assertTrue(config.getBoolean("gauge.value"));

        assertTrue(config.getBoolean("meter.count"));
        assertFalse(config.getBoolean("meter.mean"));
        assertFalse(config.getBoolean("meter.oneMinuteRate"));
        assertFalse(config.getBoolean("meter.fiveMinuteRate"));
        assertFalse(config.getBoolean("meter.fifteenMinuteRate"));

        assertEquals(Arrays.asList(0.5, 0.99), config.getDoubleList("histogram.percentiles"));
        assertFalse(config.getBoolean("histogram.count"));
        assertFalse(config.getBoolean("histogram.min"));
        assertFalse(config.getBoolean("histogram.max"));
        assertFalse(config.getBoolean("histogram.stdDev"));

        assertEquals(Arrays.asList(0.5, 0.99), config.getDoubleList("timer.percentiles"));
        assertTrue(config.getBoolean("timer.count"));
        assertFalse(config.getBoolean("timer.mean"));
        assertFalse(config.getBoolean("timer.min"));
        assertFalse(config.getBoolean("timer.max"));
        assertFalse(config.getBoolean("timer.stdDev"));
        assertFalse(config.getBoolean("timer.oneMinuteRate"));
        assertFalse(config.getBoolean("timer.fiveMinuteRate"));
        assertFalse(config.getBoolean("timer.fifteenMinuteRate"));
    }
}
