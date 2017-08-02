package com.avast.metrics.filter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReferenceConfTest {
    @Test
    public void testDefaultsLoadable() throws Exception {
        Config config = ConfigFactory.load().getConfig("metricFiltersDefaults");
        assertTrue(config.hasPath("root"));
        assertEquals("enable", config.getString("root.level"));

        List<Double> percentiles = config.getDoubleList("root.timer.percentiles");
        assertEquals(2, percentiles.size());
        assertEquals(0.5, percentiles.get(0), 0.0);
        assertEquals(0.99, percentiles.get(1), 0.0);
    }
}
