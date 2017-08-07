package com.avast.metrics.dropwizard.formatting.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FormatterConfigTest {
    @Test
    public void testLoad() throws Exception {
        Config config = ConfigFactory.load().getConfig("testFormatting");
        FormatterConfig formatterConfig = FormatterConfig.fromConfig(config);

        assertEquals(Collections.singletonList(0.42), formatterConfig.getHistogram().getPercentiles());
        assertTrue(formatterConfig.getHistogram().isMax());
    }
}
