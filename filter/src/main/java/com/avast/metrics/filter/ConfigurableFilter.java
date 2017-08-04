package com.avast.metrics.filter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configurable metrics filter.
 */
class ConfigurableFilter implements MetricsFilter {
    private static final String SEPARATOR_NAMES = ".";

    /**
     * Name of root filter (prefix of all possible names).
     */
    private static final String ROOT_FILTER_NAME = "";

    /**
     * List of configurations sorted from the most concrete ones to the less concrete ones. The first prefix that
     * matches beginning of the metric name will win.
     */
    private final List<FilterConfig> configuration;

    ConfigurableFilter(List<FilterConfig> configuration) {
        this.configuration = configuration
                .stream()
                .map(this::appendNameSeparator)
                .map(this::mapRootName)
                .sorted(Comparator.comparing(FilterConfig::getMetricName).reversed())
                .collect(Collectors.toList());

        boolean rootFilterPresent = this.configuration.stream()
                .anyMatch(cfg -> ROOT_FILTER_NAME.equals(cfg.getMetricName()));
        if (!rootFilterPresent) {
            throw new IllegalArgumentException("Root filter is missing");
        }
    }

    @Override
    public boolean isEnabled(String metricName) {
        String nameWithSeparator = appendNameSeparator(metricName);

        return configuration
                .stream()
                .filter(cfg -> nameWithSeparator.startsWith(cfg.getMetricName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Root filter should be defined, this should never happen"))
                .isEnabled();
    }

    private FilterConfig mapRootName(FilterConfig config) {
        return config.getMetricName().equals(appendNameSeparator("root"))
                ? config.withMetricName(ROOT_FILTER_NAME)
                : config;
    }

    private FilterConfig appendNameSeparator(FilterConfig config) {
        return config.withMetricName(appendNameSeparator(config.getMetricName()));
    }

    /**
     * Append name separator at the end of name to prevent bad prefix matches.
     * - Incorrect: "hello.wor" matches "hello.world"
     * - Correct: "hello.wor." doesn't match "hello.world."
     */
    private String appendNameSeparator(String metricName) {
        return (metricName.endsWith(SEPARATOR_NAMES)) ? metricName : metricName + SEPARATOR_NAMES;
    }
}
