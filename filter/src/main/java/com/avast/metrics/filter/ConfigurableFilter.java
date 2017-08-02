package com.avast.metrics.filter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configurable metrics filter.
 */
public class ConfigurableFilter implements MetricsFilter {
    private static final String SEPARATOR_NAMES = "/";

    /**
     * Name of root filter (prefix of all possible names).
     */
    private static final String ROOT_FILTER_NAME = SEPARATOR_NAMES;

    /**
     * List of configurations sorted from the most concrete ones to the less concrete ones. The first prefix that
     * matches beginning of the metric name will win.
     */
    private final List<FilterConfig> configuration;

    public ConfigurableFilter(List<FilterConfig> configuration) {
        this.configuration = configuration
                .stream()
                .map(cfg -> cfg.withMetricName(appendNameSeparator(cfg.getMetricName())))
                .sorted(Comparator.comparing(FilterConfig::getMetricName).reversed())
                .collect(Collectors.toList());

        boolean rootFilterPresent = configuration.stream()
                .anyMatch(cfg -> ROOT_FILTER_NAME.equals(cfg.getMetricName()));
        if (!rootFilterPresent) {
            throw new IllegalArgumentException("Root filter is missing");
        }
    }

    @Override
    public FilterConfig getConfig(String metricName) {
        String nameWithSeparator = appendNameSeparator(metricName);

        return configuration
                .stream()
                .filter(cfg -> nameWithSeparator.startsWith(cfg.getMetricName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Root filter should be always defined"));
    }

    /**
     * Append name separator at the end of name to prevent bad prefix matches.
     * - Incorrect: "hello/wor" matches "hello/world"
     * - Correct: "hello/wor/" doesn't match "hello/world/"
     */
    private String appendNameSeparator(String metricName) {
        return (metricName.endsWith(SEPARATOR_NAMES)) ? metricName : metricName + SEPARATOR_NAMES;
    }
}
