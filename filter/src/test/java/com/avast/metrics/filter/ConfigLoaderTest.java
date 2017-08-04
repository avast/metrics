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
        FilterConfig filterConfig = filterConfigs.get(0);
        assertEquals("root", filterConfig.getMetricName());
        assertTrue(filterConfig.isEnabled());
    }

    @Test
    public void testAllEnabled() throws Exception {
        Config config = ConfigFactory.load().getConfig("testAllEnabled");
        List<FilterConfig> filterConfigs = new ConfigLoader().load(config);

        assertEquals(1, filterConfigs.size());
        FilterConfig filterConfig = filterConfigs.get(0);
        assertEquals("root", filterConfig.getMetricName());
        assertTrue(filterConfig.isEnabled());
    }

    @Test
    public void testAllDisabled() throws Exception {
        Config config = ConfigFactory.load().getConfig("testAllDisabled");
        List<FilterConfig> filterConfigs = new ConfigLoader().load(config);

        assertEquals(1, filterConfigs.size());
        FilterConfig filterConfig = filterConfigs.get(0);
        assertEquals("root", filterConfig.getMetricName());
        assertFalse(filterConfig.isEnabled());
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

        FilterConfig sectionConfig = filterConfigs.get(0);
        assertEquals("name1.name2.nameN", sectionConfig.getMetricName());
        assertFalse(sectionConfig.isEnabled());

        FilterConfig rootFilterConfig = filterConfigs.get(1);
        assertEquals("root", rootFilterConfig.getMetricName());
        assertTrue(rootFilterConfig.isEnabled());
    }
}
