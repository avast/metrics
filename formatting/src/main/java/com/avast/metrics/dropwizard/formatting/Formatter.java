package com.avast.metrics.dropwizard.formatting;

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
}
