package com.jvprz.spectrobesreforged.common.feature.spectrobe;

import java.util.HashMap;
import java.util.Map;

public final class SpectrobeSpeciesRegistry {

    private static final Map<String, SpectrobeSpecies> SPECIES = new HashMap<>();

    private SpectrobeSpeciesRegistry() {}

    public static void register(SpectrobeSpecies species) {
        SPECIES.put(species.key(), species);
    }

    public static SpectrobeSpecies getByKey(String key) {
        return SPECIES.get(key);
    }
}