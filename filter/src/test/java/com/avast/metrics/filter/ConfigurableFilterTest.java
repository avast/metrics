package com.avast.metrics.filter;

import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigurableFilterTest {
    private MetricsFilter loadFilter(String name) {
        return MetricsFilter.fromConfig(ConfigFactory.load().getConfig(name), ".");
    }

    @Test
    public void testEmpty() throws Exception {
        MetricsFilter filter = loadFilter("testEmpty");

        assertTrue(filter.isEnabled(""));
        assertTrue(filter.isEnabled(MetricsFilter.ROOT_FILTER_NAME));
        assertTrue(filter.isEnabled("anything"));
        assertTrue(filter.isEnabled("anything.structured.name.myMeter"));

        assertEquals(1.0, filter.getConfig("").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig(MetricsFilter.ROOT_FILTER_NAME).getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("anything").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("anything.structured.name.myMeter").getSampleRate(), 0.0);
    }

    @Test
    public void testAllEnabled() throws Exception {
        MetricsFilter filter = loadFilter("testAllEnabled");

        assertTrue(filter.isEnabled(""));
        assertTrue(filter.isEnabled(MetricsFilter.ROOT_FILTER_NAME));
        assertTrue(filter.isEnabled("anything"));
        assertTrue(filter.isEnabled("anything.structured.name.myMeter"));

        assertEquals(1.0, filter.getConfig("").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig(MetricsFilter.ROOT_FILTER_NAME).getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("anything").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("anything.structured.name.myMeter").getSampleRate(), 0.0);
    }

    @Test
    public void testAllDisabled() throws Exception {
        MetricsFilter filter = loadFilter("testAllDisabled");

        assertFalse(filter.isEnabled(""));
        assertFalse(filter.isEnabled(MetricsFilter.ROOT_FILTER_NAME));
        assertFalse(filter.isEnabled("anything"));
        assertFalse(filter.isEnabled("anything.structured.name.myMeter"));

        assertEquals(0.0, filter.getConfig("").getSampleRate(), 0.0);
        assertEquals(0.0, filter.getConfig(MetricsFilter.ROOT_FILTER_NAME).getSampleRate(), 0.0);
        assertEquals(0.0, filter.getConfig("anything").getSampleRate(), 0.0);
        assertEquals(0.0, filter.getConfig("anything.structured.name.myMeter").getSampleRate(), 0.0);
    }

    @Test
    public void testStructuredName() throws Exception {
        MetricsFilter filter = loadFilter("testStructuredName");

        assertTrue(filter.isEnabled(""));
        assertTrue(filter.isEnabled(MetricsFilter.ROOT_FILTER_NAME));
        assertTrue(filter.isEnabled("name1"));
        assertTrue(filter.isEnabled("name1.name2"));
        assertFalse(filter.isEnabled("name1.name2.nameN"));
        assertTrue(filter.isEnabled("anything"));
        assertTrue(filter.isEnabled("anything.structured.name.myMeter"));

        assertEquals(1.0, filter.getConfig("").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig(MetricsFilter.ROOT_FILTER_NAME).getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("name1").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("name1.name2").getSampleRate(), 0.0);
        assertEquals(0.5, filter.getConfig("name1.name2.nameN").getSampleRate(), 0.0);
        assertEquals(0.5, filter.getConfig("name1.name2.nameN.subtree").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("anything").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("anything.structured.name.myMeter").getSampleRate(), 0.0);
    }

    @Test
    public void testComplexEnableDisable() throws Exception {
        MetricsFilter filter = loadFilter("testComplexEnableDisable");

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

        assertEquals(1.0, filter.getConfig("").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig(MetricsFilter.ROOT_FILTER_NAME).getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("myCounter").getSampleRate(), 0.0);
        assertEquals(0.0, filter.getConfig("name1").getSampleRate(), 0.0);
        assertEquals(0.0, filter.getConfig("name1.myCounter").getSampleRate(), 0.0);
        assertEquals(0.4, filter.getConfig("name1.name2").getSampleRate(), 0.0);
        assertEquals(0.4, filter.getConfig("name1.name2.myCounter").getSampleRate(), 0.0);
        assertEquals(0.5, filter.getConfig("name1.name2.nameN").getSampleRate(), 0.0);
        assertEquals(0.6, filter.getConfig("name1.name2.nameN.myCounter").getSampleRate(), 0.0);
        assertEquals(0.5, filter.getConfig("name1.name2.nameN.myMeter").getSampleRate(), 0.0);
        assertEquals(0.5, filter.getConfig("name1.name2.nameN.myTimer").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("anything").getSampleRate(), 0.0);
        assertEquals(1.0, filter.getConfig("anything.structured.name.myMeter").getSampleRate(), 0.0);
    }
}
