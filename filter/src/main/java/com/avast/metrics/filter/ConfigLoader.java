package com.avast.metrics.filter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

import java.util.List;
import java.util.stream.Collectors;

class ConfigLoader {
    static final String SECTION_DEFAULTS = "metricsFiltersDefaults";
    private static final String CONFIG_NAME_SEPARATOR = ".";
    private static final String ENABLED_SUFFIX = CONFIG_NAME_SEPARATOR + "enabled";

    private final String nameSeparator;

    ConfigLoader(String nameSeparator) {
        this.nameSeparator = nameSeparator;
    }

    List<FilterConfig> load(Config config) {
        Config referenceConfig = ConfigFactory.defaultReference().getConfig(SECTION_DEFAULTS);
        Config mergedConfig = config.withFallback(referenceConfig);

        return mergedConfig
                .entrySet()
                .stream()
                .map(section -> parseRecord(section.getKey(), mergedConfig.getBoolean(section.getKey())))
                .collect(Collectors.toList());
    }

    private FilterConfig parseRecord(String metricName, boolean enabled) {
        return new FilterConfig(parseName(metricName), enabled);
    }

    /**
     * Remove suffix from name and replace separator characters.
     */
    private String parseName(String metricName) {
        if (!metricName.endsWith(ENABLED_SUFFIX)) {
            throw new ConfigException.BadPath(metricName, "Expecting '" + ENABLED_SUFFIX + "' suffix");
        }

        return metricName.substring(0, metricName.length() - ENABLED_SUFFIX.length())
                .replace(CONFIG_NAME_SEPARATOR, nameSeparator);
    }
}
