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

        assertEquals("enabled", config.getString("counter.count"));

        assertEquals("enabled", config.getString("gauge.value"));

        assertEquals("enabled", config.getString("meter.count"));
        assertEquals("disabled", config.getString("meter.mean"));
        assertEquals("disabled", config.getString("meter.oneMinuteRate"));
        assertEquals("disabled", config.getString("meter.fiveMinuteRate"));
        assertEquals("disabled", config.getString("meter.fifteenMinuteRate"));

        List<Double> timerPercentiles = config.getDoubleList("histogram.percentiles");
        assertEquals(2, timerPercentiles.size());
        assertEquals(0.5, timerPercentiles.get(0), 0.0);
        assertEquals(0.99, timerPercentiles.get(1), 0.0);
        assertEquals("disabled", config.getString("histogram.count"));
        assertEquals("disabled", config.getString("histogram.min"));
        assertEquals("disabled", config.getString("histogram.max"));
        assertEquals("disabled", config.getString("histogram.stdDev"));

        List<Double> histogramPercentiles = config.getDoubleList("timer.percentiles");
        assertEquals(2, histogramPercentiles.size());
        assertEquals(0.5, histogramPercentiles.get(0), 0.0);
        assertEquals(0.99, histogramPercentiles.get(1), 0.0);
        assertEquals("enabled", config.getString("timer.count"));
        assertEquals("disabled", config.getString("timer.mean"));
        assertEquals("disabled", config.getString("timer.min"));
        assertEquals("disabled", config.getString("timer.max"));
        assertEquals("disabled", config.getString("timer.stdDev"));
        assertEquals("disabled", config.getString("timer.oneMinuteRate"));
        assertEquals("disabled", config.getString("timer.fiveMinuteRate"));
        assertEquals("disabled", config.getString("timer.fifteenMinuteRate"));
    }
}
