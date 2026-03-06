package com.jvprz.spectrobesreforged.common.feature.spectrobe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class SpectrobeSpeciesRegistry {

    private static final Map<String, SpectrobeSpecies> SPECIES = new HashMap<>();

    private SpectrobeSpeciesRegistry() {}

    public static void register(SpectrobeSpecies species) {
        String key = normalize(species.key());

        if (SPECIES.containsKey(key)) {
            throw new IllegalStateException("Duplicate spectrobe species key: " + key);
        }

        SPECIES.put(key, species);
    }

    public static SpectrobeSpecies getByKey(String key) {
        if (key == null) return null;
        return SPECIES.get(normalize(key));
    }

    public static boolean exists(String key) {
        if (key == null) return false;
        return SPECIES.containsKey(normalize(key));
    }

    public static Collection<SpectrobeSpecies> all() {
        return SPECIES.values();
    }

    public static Collection<String> keys() {
        return SPECIES.keySet();
    }

    public static void clear() {
        SPECIES.clear();
    }

    private static String normalize(String key) {
        return key.trim().toLowerCase(Locale.ROOT);
    }
}