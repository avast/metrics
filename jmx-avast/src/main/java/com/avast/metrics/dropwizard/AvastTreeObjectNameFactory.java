package com.avast.metrics.dropwizard;

import com.codahale.metrics.DefaultObjectNameFactory;
import com.codahale.metrics.ObjectNameFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This is the Avast alternative for {@link TreeObjectNameFactory}. It uses "type-scope-name" format of resulting {@link ObjectName} (levels
 * 3-N are glued together).
 * See unit tests.
 */
public class AvastTreeObjectNameFactory implements ObjectNameFactory {

    public static final String SEPARATOR = "@#*";

    private static final ObjectNameFactory defaultFactory = new DefaultObjectNameFactory();

    private static final String[] partNames = {"type", "scope", "name"};

    private AvastTreeObjectNameFactory() {
    }

    public static AvastTreeObjectNameFactory getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public ObjectName createName(String type, String domain, String name) {
        Optional<ObjectName> parsedName = parseName(domain, name);
        return parsedName.orElse(defaultFactory.createName(type, domain, name));
    }

    private Optional<ObjectName> parseName(String domain, String name) {
        try {
            final String[] parts = name.split(Pattern.quote(SEPARATOR), partNames.length);

            /*
            Following block of code is a little hack.
            The problem is the `ObjectName` requires `HashTable` as parameter but the `HashTable` is unsorted and
            thus unusable for us. We hack it by raping the `HashTable` and in-fact using `LinkedHashMap` which is
            much more suitable for our needs.
             */

            final LinkedHashMap<String, String> map = new LinkedHashMap<>();
            final Hashtable<String, String> properties = new Hashtable<String, String>() {
                @Override
                public Set<Map.Entry<String, String>> entrySet() {
                    return map.entrySet();
                }
            };

            for (int i = 0; i < parts.length; i++) {
                properties.put(partNames[i], quote(parts[i]));
                map.put(partNames[i], quote(parts[i]));
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

    private static class Holder {
        static final AvastTreeObjectNameFactory INSTANCE = new AvastTreeObjectNameFactory();
    }
}
