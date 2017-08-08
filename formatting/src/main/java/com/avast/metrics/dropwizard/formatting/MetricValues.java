package com.avast.metrics.dropwizard.formatting;

import java.util.Map;

/**
 * Single metric and its values.
 */
public class MetricValues {
    private final String name;
    private final Map<String, String> fieldsValues;

    public MetricValues(String name, Map<String, String> fieldsValues) {
        this.name = name;
        this.fieldsValues = fieldsValues;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getFieldsValues() {
        return fieldsValues;
    }

    @Override
    public String toString() {
        return name + " " + fieldsValues;
    }
}
