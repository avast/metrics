package com.avast.metrics.filter;

/**
 * All possible filter levels for metrics defined in application code.
 */
public enum FilterLevel {
    DISABLE {
        @Override
        boolean isEnabled() {
            return false;
        }
    },

    ENABLE {
        @Override
        boolean isEnabled() {
            return true;
        }
    };

    abstract boolean isEnabled();
}
