package com.avast.metrics.filter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReferenceConfTest {
    @Test
    public void testDefaultsLoadable() throws Exception {
        Config config = ConfigFactory.load().getConfig(ConfigLoader.SECTION_DEFAULTS);
        assertEquals("enabled", config.getString("root"));

        assertEquals(1, config.entrySet().size());
    }
}
