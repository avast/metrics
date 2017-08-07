package com.avast.metrics.dropwizard.formatting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Graphite/StatsD formatter. Timestamp is NOT appended.
 * <p>
 * http://graphite.readthedocs.io/en/latest/feeding-carbon.html#the-plaintext-protocol
 * <metric path> <metric value> <metric timestamp>
 */
public class GraphiteFormatter implements Formatter {
    private static final String SEPARATOR_NAME_PARTS = ".";
    private static final String SEPARATOR_NAME_VALUE = " ";
    private static final String SEPARATOR_METRICS = "\n";

    @Override
    public String nameSeparator() {
        return SEPARATOR_NAME_PARTS;
    }

    @Override
    public String sanitizeName(String namePart) {
        StringBuilder result = new StringBuilder(namePart.length());

        for (int i = 0; i < namePart.length(); ++i) {
            char c = namePart.charAt(i);

            switch (c) {
                case '.':    // Separator of metric name parts in Graphite
                case '/':    // Separator of directories in Graphite
                case ':':    // Separator of fields in StatsD protocol
                case '|':    // Separator of fields in StatsD protocol
                case '@':    // Separator of fields in StatsD protocol
                case '\n':   // Separator of multiple metrics in StatsD protocol
                case '=':    // Query can be confused
                case '(':    // Query can be confused
                case ')':    // Query can be confused
                    result.append('-');
                    break;

                case ' ':    // Separator of fields in Graphite data files
                    result.append('_');
                    break;

                default:
                    result.append(c);
                    break;
            }
        }

        return result.toString();
    }

    @Override
    public String formatNumber(long number) {
        return Long.toString(number);
    }

    @Override
    public String formatNumber(double number) {
        return String.format(Locale.ENGLISH, "%s", number);
    }

    @Override
    public <T> String formatObject(T object) {
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
        } else {
            return object.toString(); // TODO: This may break the format
        }
    }

    @Override
    public String format(Stream<MetricValues> metrics) {
        return metrics
                .flatMap(this::formatValues)
                .collect(Collectors.joining(SEPARATOR_METRICS));
    }

    private Stream<String> formatValues(MetricValues values) {
        return values
                .getFieldsValues()
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(fieldValue -> values.getName() + SEPARATOR_NAME_PARTS + fieldValue.getKey()
                        + SEPARATOR_NAME_VALUE + fieldValue.getValue());
    }
}
