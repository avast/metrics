package com.avast.metrics.filter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ConfigLoader {
    static final String SECTION_DEFAULTS = "metricsFiltersDefaults";
    private static final String SEPARATOR = ".";

    private static final String ENABLED = SEPARATOR + "enabled";
    private static final String SAMPLE_RATE = SEPARATOR + "sampleRate";

    private final String nameSeparator;

    ConfigLoader(String nameSeparator) {
        this.nameSeparator = nameSeparator;
    }

    List<FilterConfig> load(Config config) {
        Config referenceConfig = ConfigFactory.defaultReference().getConfig(SECTION_DEFAULTS);
        Config mergedConfig = config.withFallback(referenceConfig);

        Set<String> metricNames = mergedConfig
                .entrySet()
                .stream()
                .map(section -> metricName(section.getKey()))
                .collect(Collectors.toSet());

        return metricNames
                .stream()
                .map(metricName -> parseConfig(mergedConfig, metricName))
                .collect(Collectors.toList());
    }

    private String metricName(String metricNameWithSuffix) {
        if (metricNameWithSuffix.endsWith(ENABLED)) {
            return metricNameWithSuffix.substring(0, metricNameWithSuffix.length() - ENABLED.length());
        } else if (metricNameWithSuffix.endsWith(SAMPLE_RATE)) {
            return metricNameWithSuffix.substring(0, metricNameWithSuffix.length() - SAMPLE_RATE.length());
        } else {
            throw new ConfigException.BadPath(metricNameWithSuffix,
                    "Expecting metric name with '" + ENABLED + "|" + SAMPLE_RATE + "' suffix");
        }
    }

    private FilterConfig parseConfig(Config config, String metricName) {
        String finalName = metricName.replace(SEPARATOR, nameSeparator);
        boolean enabled = config.getBoolean(metricName + ENABLED);
        double sampleRate = config.hasPath(metricName + SAMPLE_RATE)
                ? config.getDouble(metricName + SAMPLE_RATE)
                : FilterConfig.enabledToSampleRate(enabled);

        return new FilterConfig(finalName, enabled, sampleRate);
    }
}
