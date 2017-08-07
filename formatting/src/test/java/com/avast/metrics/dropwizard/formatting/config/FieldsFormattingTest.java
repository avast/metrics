package com.avast.metrics.dropwizard.formatting.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldsFormattingTest {
    @Test
    public void testLoad() throws Exception {
        Config config = ConfigFactory.load().getConfig("testFormatting");
        FieldsFormatting formattingConfig = FieldsFormatting.fromConfig(config);

        assertEquals(Collections.singletonList(0.42), formattingConfig.getHistogram().getPercentiles());
        assertTrue(formattingConfig.getHistogram().isMax());
    }
}
