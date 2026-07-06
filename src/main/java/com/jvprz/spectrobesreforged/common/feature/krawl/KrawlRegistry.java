package com.jvprz.spectrobesreforged.common.feature.krawl;

import com.jvprz.spectrobesreforged.common.feature.krawl.data.KrawlDefinition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class KrawlRegistry {

    private static final Map<String, KrawlDefinition> KRAWLS = new HashMap<>();

    private KrawlRegistry() {}

    public static void register(KrawlDefinition krawl) {
        String key = normalize(krawl.key());

        if (KRAWLS.containsKey(key)) {
            throw new IllegalStateException("Duplicate krawl key: " + key);
        }

        KRAWLS.put(key, krawl);
    }

    public static KrawlDefinition getByKey(String key) {
        if (key == null) return null;
        return KRAWLS.get(normalize(key));
    }

    public static boolean exists(String key) {
        if (key == null) return false;
        return KRAWLS.containsKey(normalize(key));
    }

    public static Collection<KrawlDefinition> all() {
        return KRAWLS.values();
    }

    public static Collection<String> keys() {
        return KRAWLS.keySet();
    }

    public static void clear() {
        KRAWLS.clear();
    }

    private static String normalize(String key) {
        return key.trim().toLowerCase(Locale.ROOT);
    }
}