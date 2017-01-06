package com.avast.metrics.dropwizard;

import com.codahale.metrics.DefaultObjectNameFactory;
import com.codahale.metrics.ObjectNameFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class TreeObjectNameFactory implements ObjectNameFactory {

    public static final String SEPARATOR = "@#*";

    private static final ObjectNameFactory defaultFactory = new DefaultObjectNameFactory();

    private static final String[] partNames = {"type", "scope", "name"};

    private TreeObjectNameFactory() {
    }

    public static TreeObjectNameFactory getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public ObjectName createName(String type, String domain, String name) {
        Optional<ObjectName> parsedName = parseName(domain, name);
        return parsedName.orElse(defaultFactory.createName(type, domain, name));
    }

    private Optional<ObjectName> parseName(String domain, String name) {
        try {
            String[] parts = name.split(Pattern.quote(SEPARATOR), partNames.length);

            Hashtable<String, String> properties = new OrderedProperties();
            for (int i = 0; i < parts.length; i++) {
                properties.put(partNames[i], quote(parts[i]));
            }

            return Optional.of(new ObjectName(domain, properties));
        } catch (MalformedObjectNameException ex) {
            return Optional.empty();
        }
    }

    private String quote(String objectName) {
        return objectName
                .replaceAll(Pattern.quote(SEPARATOR), "/")
                .replaceAll("[\\Q.?*\"\\E]", "_");
    }

    private static class OrderedProperties extends Hashtable<String, String> {

        @Override
        public Set<Map.Entry<String, String>> entrySet() {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            for (String partName : partNames) {
                if (get(partName) != null) {
                    map.put(partName, get(partName));
                }
            }
            return map.entrySet();
        }
    }

    private static class Holder {
        static final TreeObjectNameFactory INSTANCE = new TreeObjectNameFactory();
    }
}
