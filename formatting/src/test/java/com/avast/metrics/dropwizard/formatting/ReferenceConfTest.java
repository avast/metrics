package com.avast.metrics.dropwizard.formatting;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReferenceConfTest {
    @Test
    public void testDefaultsLoadable() throws Exception {
        Config config = ConfigFactory.load().getConfig("formattingDefaults");

        assertEquals("enable", config.getString("counter.count"));

        assertEquals("enable", config.getString("gauge.value"));

        assertEquals("enable", config.getString("meter.count"));
        assertEquals("disable", config.getString("meter.mean"));
        assertEquals("disable", config.getString("meter.oneMinuteRate"));
        assertEquals("disable", config.getString("meter.fiveMinuteRate"));
        assertEquals("disable", config.getString("meter.fifteenMinuteRate"));

        List<Double> timerPercentiles = config.getDoubleList("histogram.percentiles");
        assertEquals(2, timerPercentiles.size());
        assertEquals(0.5, timerPercentiles.get(0), 0.0);
        assertEquals(0.99, timerPercentiles.get(1), 0.0);
        assertEquals("disable", config.getString("histogram.count"));
        assertEquals("disable", config.getString("histogram.min"));
        assertEquals("disable", config.getString("histogram.max"));
        assertEquals("disable", config.getString("histogram.stdDev"));

        List<Double> histogramPercentiles = config.getDoubleList("timer.percentiles");
        assertEquals(2, histogramPercentiles.size());
        assertEquals(0.5, histogramPercentiles.get(0), 0.0);
        assertEquals(0.99, histogramPercentiles.get(1), 0.0);
        assertEquals("enable", config.getString("timer.count"));
        assertEquals("disable", config.getString("timer.mean"));
        assertEquals("disable", config.getString("timer.min"));
        assertEquals("disable", config.getString("timer.max"));
        assertEquals("disable", config.getString("timer.stdDev"));
        assertEquals("disable", config.getString("timer.oneMinuteRate"));
        assertEquals("disable", config.getString("timer.fiveMinuteRate"));
        assertEquals("disable", config.getString("timer.fifteenMinuteRate"));
    }
}
