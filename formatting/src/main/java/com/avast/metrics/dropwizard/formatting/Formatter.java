package com.avast.metrics.dropwizard.formatting;

import java.util.stream.Stream;

/**
 * Generic formatter of metric names and values.
 */
public interface Formatter {
    /**
     * Separator of name parts.
     */
    String nameSeparator();

    /**
     * Sanitize part of a metric name to not contain any illegal character. Escape, replace or delete them.
     */
    String sanitizeName(String namePart);

    /**
     * Format long number to string.
     */
    String formatNumber(long number);

    /**
     * Format double number to string.
     */
    String formatNumber(double number);

    /**
     * Format an object of unknown type to string.
     */
    <T> String formatObject(T object);

    /**
     * Format metrics and their values.
     */
    String format(Stream<MetricValue> metrics);
}
