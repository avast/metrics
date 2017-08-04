package com.avast.metrics.filter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;
import java.util.stream.Collectors;

class ConfigLoader {
    static final String SECTION_DEFAULTS = "metricsFiltersDefaults";
    private static final char CONFIG_SEPARATOR_CHAR = '/';

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
        return new FilterConfig(parseName(metricName), parseEnableString(metricName, enabled));
    }

    /**
     * Replace all '/' by '.' and trim quotes added by Typesafe config.
     * <p>
     * It's impossible to use '.' as a separator in Typesafe config. Value for a key may be either string or config
     * but not both at the same time. Latter definitions replace all preceding ones.
     * <p>
     * name1 = disabled
     * name1.name2.nameN.myCounter = enabled
     */
    private String parseName(String metricName) {
        String name = metricName.replace(CONFIG_SEPARATOR_CHAR, MetricsFilter.NAME_SEPARATOR_CHAR);
        int len = name.length();

        if (len >= 2 && name.charAt(0) == '"' && name.charAt(len - 1) == '"') {
            return name.substring(1, len - 1);
        } else {
            return name;
        }
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
