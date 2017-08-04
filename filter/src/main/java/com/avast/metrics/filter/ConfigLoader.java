package com.avast.metrics.filter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;
import java.util.stream.Collectors;

class ConfigLoader {
    static final String SECTION_DEFAULTS = "metricsFiltersDefaults";

    List<FilterConfig> load(Config config) {
        Config referenceConfig = ConfigFactory.defaultReference().getConfig(SECTION_DEFAULTS);
        Config mergedConfig = config.withFallback(referenceConfig);

        return mergedConfig
                .entrySet()
                .stream()
                .map(section -> parseRecord(section.getKey(), mergedConfig.getString(section.getKey())))
                .collect(Collectors.toList());
    }

    private FilterConfig parseRecord(String metricName, String enabled) {
        return new FilterConfig(metricName, parseEnableString(metricName, enabled));
    }

    private boolean parseEnableString(String metricName, String enabled) {
        switch (enabled) {
            case "enabled":
                return true;
            case "disabled":
                return false;
            default:
                throw new IllegalArgumentException("Invalid value, 'enabled' or 'disabled' expected: "
                        + metricName + ", " + enabled);
        }
    }
}
