package com.avast.metrics.filter;

import com.typesafe.config.ConfigFactory;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigurableFilterTest {
    @Test
    public void testEmpty() throws Exception {
        MetricsFilter filter = MetricsFilter.fromConfig(ConfigFactory.load().getConfig("testEmpty"));
        assertTrue(filter.isEnabled(""));
        assertTrue(filter.isEnabled(MetricsFilter.ROOT_FILTER_NAME));

        assertTrue(filter.isEnabled("anything"));
        assertTrue(filter.isEnabled("anything.structured.name.myMeter"));
    }

    @Test
    public void testAllEnabled() throws Exception {
        MetricsFilter filter = MetricsFilter.fromConfig(ConfigFactory.load().getConfig("testAllEnabled"));
        assertTrue(filter.isEnabled(""));
        assertTrue(filter.isEnabled(MetricsFilter.ROOT_FILTER_NAME));

        assertTrue(filter.isEnabled("anything"));
        assertTrue(filter.isEnabled("any.structured.name.myMeter"));
    }

    @Test
    public void testAllDisabled() throws Exception {
        MetricsFilter filter = MetricsFilter.fromConfig(ConfigFactory.load().getConfig("testAllDisabled"));
        assertFalse(filter.isEnabled(""));
        assertFalse(filter.isEnabled(MetricsFilter.ROOT_FILTER_NAME));

        assertFalse(filter.isEnabled("anything"));
        assertFalse(filter.isEnabled("anything.structured.name.myMeter"));
    }

    @Test
    public void testStructuredName() throws Exception {
        MetricsFilter filter = MetricsFilter.fromConfig(ConfigFactory.load().getConfig("testStructuredName"));
        assertTrue(filter.isEnabled(""));
        assertTrue(filter.isEnabled(MetricsFilter.ROOT_FILTER_NAME));
        assertTrue(filter.isEnabled("name1"));
        assertTrue(filter.isEnabled("name1.name2"));
        assertFalse(filter.isEnabled("name1.name2.nameN"));

        assertTrue(filter.isEnabled("anything"));
        assertTrue(filter.isEnabled("anything.structured.name.myMeter"));
    }

    @Test
    public void testComplexEnableDisable() throws Exception {
        MetricsFilter filter = MetricsFilter.fromConfig(ConfigFactory.load().getConfig("testComplexEnableDisable"));
        assertTrue(filter.isEnabled(""));
        assertTrue(filter.isEnabled(MetricsFilter.ROOT_FILTER_NAME));
        assertTrue(filter.isEnabled("myCounter"));
        assertFalse(filter.isEnabled("name1"));
        assertFalse(filter.isEnabled("name1.myCounter"));
        assertTrue(filter.isEnabled("name1.name2"));
        assertTrue(filter.isEnabled("name1.name2.myCounter"));
        assertFalse(filter.isEnabled("name1.name2.nameN"));
        assertTrue(filter.isEnabled("name1.name2.nameN.myCounter"));
        assertFalse(filter.isEnabled("name1.name2.nameN.myMeter"));
        assertFalse(filter.isEnabled("name1.name2.nameN.myTimer"));

        assertTrue(filter.isEnabled("anything"));
        assertTrue(filter.isEnabled("anything.structured.name.myMeter"));
    }
}
