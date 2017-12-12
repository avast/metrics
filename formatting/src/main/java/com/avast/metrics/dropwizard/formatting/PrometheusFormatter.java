package com.avast.metrics.dropwizard.formatting;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Prometheus formatter.
 * <p>
 * https://prometheus.io/docs/instrumenting/exposition_formats/
 * https://prometheus.io/docs/concepts/data_model/
 * &lt;metric name&gt; value
 * &lt;metric name&gt;{&lt;label name&gt;=&lt;label value&gt;, ...} value
 */
public class PrometheusFormatter implements Formatter {
    private static final String SEPARATOR_NAME_PARTS = "_";
    private static final String SEPARATOR_NAME_VALUE = " ";
    private static final String SEPARATOR_METRICS = "\n";

    @Override
    public String nameSeparator() {
        return SEPARATOR_NAME_PARTS;
    }

    @Override
    public String sanitizeName(String namePart) {
        if (namePart.isEmpty()) {
            throw new IllegalArgumentException("Name is empty string");
        }

        StringBuilder result = new StringBuilder(namePart.length());

        for (int i = 0; i < namePart.length(); ++i) {
            char c = namePart.charAt(i);

            // Regexp for full name is [a-zA-Z_:][a-zA-Z0-9_:]*
            // '_' is separator of parts
            if (c >= 'a' && c <= 'z'
                    || c >= 'A' && c <= 'Z'
                    || c >= '0' && c <= '9'
                    || c == ':') {
                result.append(c);
            }
            // else remove the character, there is no usable replacement in the set of allowed characters
        }

        return result.toString();
    }

    @Override
    public String contentType() {
        return "text/plain; version=0.0.4";
    }

    @Override
    public String format(Stream<MetricValue> metrics) {
        return metrics
                .map(metric -> metric.getName() + SEPARATOR_NAME_VALUE + metric.getValue())
                .collect(Collectors.joining(SEPARATOR_METRICS, "", SEPARATOR_METRICS));
    }
}
