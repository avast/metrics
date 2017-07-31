package com.avast.metrics.dropwizard.formatting;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GraphiteFormatterTest {
    @Test
    public void testSanitize() throws Exception {
        assertEquals("-_-------abcdefgh-_-------abcdefgh",
                new GraphiteFormatter().sanitizeName(". :|@\n=()abcdefgh. :|@\n=()abcdefgh"));
    }
}
