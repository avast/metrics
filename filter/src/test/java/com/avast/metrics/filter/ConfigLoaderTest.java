package com.avast.metrics.filter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ConfigLoaderTest {
    private List<FilterConfig> loadConfig(String name) {
        Config config = ConfigFactory.load().getConfig(name);
        return new ConfigLoader(".").load(config);
    }

    @Test
    public void testEmpty() throws Exception {
        List<FilterConfig> filterConfigs = loadConfig("testEmpty");

        List<FilterConfig> expected = Collections.singletonList(
                new FilterConfig(MetricsFilter.ROOT_FILTER_NAME, true));

        assertEquals(expected, filterConfigs);
    }

    @Test
    public void testAllEnabled() throws Exception {
        List<FilterConfig> filterConfigs = loadConfig("testAllEnabled");

        List<FilterConfig> expected = Collections.singletonList(
                new FilterConfig(MetricsFilter.ROOT_FILTER_NAME, true));

        assertEquals(expected, filterConfigs);
    }

    @Test
    public void testAllDisabled() throws Exception {
        List<FilterConfig> filterConfigs = loadConfig("testAllDisabled");

        List<FilterConfig> expected = Collections.singletonList(
                new FilterConfig(MetricsFilter.ROOT_FILTER_NAME, false));

        assertEquals(expected, filterConfigs);
    }

    @Test(expected = ConfigException.WrongType.class)
    public void testBroken() throws Exception {
        loadConfig("testBroken"); // Exception
    }

    @Test(expected = ConfigException.WrongType.class)
    public void testEnabledBroken() throws Exception {
        loadConfig("testBrokenEnabled"); // Exception
    }

    @Test(expected = ConfigException.BadPath.class)
    public void testBrokenDisabled() throws Exception {
        loadConfig("testBrokenDisabled"); // Exception
    }

    @Test
    public void testStructuredName() throws Exception {
        List<FilterConfig> filterConfigs = loadConfig("testStructuredName")
                .stream()
                .sorted(Comparator.comparing(FilterConfig::getMetricName))
                .collect(Collectors.toList());

        List<FilterConfig> expected = Arrays.asList(
                new FilterConfig("name1.name2.nameN", false),
                new FilterConfig(MetricsFilter.ROOT_FILTER_NAME, true));

        assertEquals(expected, filterConfigs);
    }

    @Test
    public void testComplexEnableDisable() throws Exception {
        List<FilterConfig> filterConfigs = loadConfig("testComplexEnableDisable")
                .stream()
                .sorted(Comparator.comparing(FilterConfig::getMetricName))
                .collect(Collectors.toList());

        List<FilterConfig> expected = Arrays.asList(
                new FilterConfig("name1", false),
                new FilterConfig("name1.name2", true),
                new FilterConfig("name1.name2.nameN", false),
                new FilterConfig("name1.name2.nameN.myCounter", true),
                new FilterConfig(MetricsFilter.ROOT_FILTER_NAME, true));

        assertEquals(expected, filterConfigs);
    }
}
