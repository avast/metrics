package com.avast.metrics.dropwizard.formatter;

/**
 * Graphite/StatsD formatter.
 */
public class GraphiteFormatter implements Formatter {
    @Override
    public String nameSeparator() {
        return ".";
    }

    @Override
    public String sanitizeName(String namePart) {
        StringBuilder result = new StringBuilder(namePart.length());

        for (int i = 0; i < namePart.length(); ++i) {
            char c = namePart.charAt(i);

            switch (c) {
                case '.':    // Separator of metric name parts in Graphite
                case '/':    // Separator of directories in Graphite
                case ':':    // Separator of fields in StatsD protocol
                case '|':    // Separator of fields in StatsD protocol
                case '@':    // Separator of fields in StatsD protocol
                case '\n':   // Separator of multiple metrics in StatsD protocol
                case '=':    // Query can be confused
                case '(':    // Query can be confused
                case ')':    // Query can be confused
                    result.append('-');
                    break;

                case ' ':    // Separator of fields in Graphite data files
                    result.append('_');
                    break;

                default:
                    result.append(c);
                    break;
            }
        }

        return result.toString();
    }
}
