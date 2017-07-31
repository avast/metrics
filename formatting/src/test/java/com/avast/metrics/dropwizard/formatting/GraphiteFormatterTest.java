package com.avast.metrics.dropwizard.formatting;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class GraphiteFormatterTest {
    private static final GraphiteFormatter formatter = new GraphiteFormatter();

    @Test
    public void testSanitize() throws Exception {
        assertEquals("-_-------abcdefgh-_-------abcdefgh",
                formatter.sanitizeName(". :|@\n=()abcdefgh. :|@\n=()abcdefgh"));
    }

    private void testFormatDoubleWithChangedDefaultLocale(Locale locale) throws Exception {
        Locale.setDefault(locale);
        assertEquals("0.0", formatter.formatNumber(0.0));
        assertEquals("1.0", formatter.formatNumber(1.0));
        assertEquals("-1.0", formatter.formatNumber(-1.0));
        assertEquals("3.14", formatter.formatNumber(3.14));
        assertEquals("3.141592653589793", formatter.formatNumber(Math.PI));
        assertEquals("1.7976931348623157E308", formatter.formatNumber(Double.MAX_VALUE));
        assertEquals("4.9E-324", formatter.formatNumber(Double.MIN_VALUE));
    }

    @Test
    public void testFormatDouble() throws Exception {
        Locale defaultLocale = Locale.getDefault();
        testFormatDoubleWithChangedDefaultLocale(Locale.ENGLISH);
        testFormatDoubleWithChangedDefaultLocale(Locale.GERMAN);
        testFormatDoubleWithChangedDefaultLocale(Locale.FRENCH);
        testFormatDoubleWithChangedDefaultLocale(Locale.CHINESE);
        testFormatDoubleWithChangedDefaultLocale(defaultLocale);
    }

    private void testFormatLongWithChangedDefaultLocale(Locale locale) throws Exception {
        Locale.setDefault(locale);
        assertEquals("0", formatter.formatNumber(0L));
        assertEquals("1", formatter.formatNumber(1L));
        assertEquals("-1", formatter.formatNumber(-1L));
        assertEquals("9223372036854775807", formatter.formatNumber(Long.MAX_VALUE));
        assertEquals("-9223372036854775808", formatter.formatNumber(Long.MIN_VALUE));
    }

    @Test
    public void testFormatLong() throws Exception {
        Locale defaultLocale = Locale.getDefault();
        testFormatLongWithChangedDefaultLocale(Locale.ENGLISH);
        testFormatLongWithChangedDefaultLocale(Locale.GERMAN);
        testFormatLongWithChangedDefaultLocale(Locale.FRENCH);
        testFormatLongWithChangedDefaultLocale(Locale.CHINESE);
        testFormatLongWithChangedDefaultLocale(defaultLocale);
    }
}
