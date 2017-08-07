package com.avast.metrics.dropwizard.formatting;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    default <T> String formatObject(T object) {
        if (object == null) {
            return "null";
        } else if (object instanceof Float) {
            return formatNumber(((Float) object).doubleValue());
        } else if (object instanceof Double) {
            return formatNumber((Double) object);
        } else if (object instanceof Byte) {
            return formatNumber(((Byte) object).longValue());
        } else if (object instanceof Short) {
            return formatNumber(((Short) object).longValue());
        } else if (object instanceof Integer) {
            return formatNumber(((Integer) object).longValue());
        } else if (object instanceof Long) {
            return formatNumber((Long) object);
        } else if (object instanceof BigInteger) {
            return formatNumber(((BigInteger) object).doubleValue());
        } else if (object instanceof BigDecimal) {
            return formatNumber(((BigDecimal) object).doubleValue());
        } else if (object instanceof Boolean) {
            return formatNumber(((Boolean) object) ? 1 : 0);
        } else if (object instanceof Character) {
            return String.valueOf(object);
        } else {
            // This may significantly break the format (newlines, invalid characters), be strict
            // return object.toString();
            return "unsupported";
        }
    }

    /**
     * Format metrics and their values.
     */
    String format(Stream<MetricValues> metrics);
}
