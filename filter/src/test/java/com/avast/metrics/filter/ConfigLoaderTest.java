package com.avast.metrics.filter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ConfigLoaderTest {
    @Test
    public void testEmpty() throws Exception {
        Config config = ConfigFactory.load().getConfig("testEmpty");
        List<FilterConfig> filterConfigs = new ConfigLoader().load(config);

        assertEquals(1, filterConfigs.size());
        assertEquals(MetricsFilter.ROOT_FILTER_NAME, filterConfigs.get(0).getMetricName());
        assertTrue(filterConfigs.get(0).isEnabled());
    }

    @Test
    public void testAllEnabled() throws Exception {
        Config config = ConfigFactory.load().getConfig("testAllEnabled");
        List<FilterConfig> filterConfigs = new ConfigLoader().load(config);

        assertEquals(1, filterConfigs.size());
        assertEquals(MetricsFilter.ROOT_FILTER_NAME, filterConfigs.get(0).getMetricName());
        assertTrue(filterConfigs.get(0).isEnabled());
    }

    @Test
    public void testAllDisabled() throws Exception {
        Config config = ConfigFactory.load().getConfig("testAllDisabled");
        List<FilterConfig> filterConfigs = new ConfigLoader().load(config);

        assertEquals(1, filterConfigs.size());
        assertEquals(MetricsFilter.ROOT_FILTER_NAME, filterConfigs.get(0).getMetricName());
        assertFalse(filterConfigs.get(0).isEnabled());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBroken() throws Exception {
        Config config = ConfigFactory.load().getConfig("testBroken");
        new ConfigLoader().load(config);
    }

    @Test
    public void testStructuredName() throws Exception {
        Config config = ConfigFactory.load().getConfig("testStructuredName");
        List<FilterConfig> filterConfigs = new ConfigLoader().load(config)
                .stream()
                .sorted(Comparator.comparing(FilterConfig::getMetricName))
                .collect(Collectors.toList());

        assertEquals(2, filterConfigs.size());

        assertEquals("name1.name2.nameN", filterConfigs.get(0).getMetricName());
        assertFalse(filterConfigs.get(0).isEnabled());

        assertEquals(MetricsFilter.ROOT_FILTER_NAME, filterConfigs.get(1).getMetricName());
        assertTrue(filterConfigs.get(1).isEnabled());
    }

    @Test
    public void testComplexEnableDisable() throws Exception {
        Config config = ConfigFactory.load().getConfig("testComplexEnableDisable");
        List<FilterConfig> filterConfigs = new ConfigLoader().load(config)
                .stream()
                .sorted(Comparator.comparing(FilterConfig::getMetricName))
                .collect(Collectors.toList());

        assertEquals(5, filterConfigs.size());

        assertEquals("name1", filterConfigs.get(0).getMetricName());
        assertFalse(filterConfigs.get(0).isEnabled());

        assertEquals("name1.name2", filterConfigs.get(1).getMetricName());
        assertTrue(filterConfigs.get(1).isEnabled());

        assertEquals("name1.name2.nameN", filterConfigs.get(2).getMetricName());
        assertFalse(filterConfigs.get(2).isEnabled());

        assertEquals("name1.name2.nameN.myCounter", filterConfigs.get(3).getMetricName());
        assertTrue(filterConfigs.get(3).isEnabled());

        assertEquals(MetricsFilter.ROOT_FILTER_NAME, filterConfigs.get(4).getMetricName());
        assertTrue(filterConfigs.get(4).isEnabled());
    }
}
