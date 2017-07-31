package com.avast.metrics.dropwizard.formatting;

/**
 * Single primitive metric and its value.
 */
public class MetricValue {
    private final String name;
    private final String value;

    public MetricValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + " " + value;
    }
}
