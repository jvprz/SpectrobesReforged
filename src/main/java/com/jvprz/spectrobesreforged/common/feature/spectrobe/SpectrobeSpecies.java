package com.jvprz.spectrobesreforged.common.feature.spectrobe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SpectrobeSpecies {

    private final int id;
    private final String key;
    private final SpectrobeType type;
    private final SpectrobeStage initialStage;
    private final int range;
    private final SpectrobeStats stats;
    private final EvolutionData evolution;
    private final List<String> textures;

    public SpectrobeSpecies(
            int id,
            String key,
            SpectrobeType type,
            SpectrobeStage initialStage,
            int range,
            SpectrobeStats stats,
            EvolutionData evolution,
            List<String> textures
    ) {
        this.id = id;
        this.key = normalizeKey(key);
        this.type = type;
        this.initialStage = initialStage;
        this.range = clamp(range, 1, 10);
        this.stats = stats != null ? stats : SpectrobeStats.empty();
        this.evolution = evolution != null ? evolution : EvolutionData.none();
        this.textures = normalizeTextures(this.key, textures);
    }

    public int id() { return id; }
    public String key() { return key; }
    public SpectrobeType type() { return type; }
    public SpectrobeStage initialStage() { return initialStage; }
    public int range() { return range; }
    public SpectrobeStats stats() { return stats; }
    public EvolutionData evolution() { return evolution; }
    public List<String> textures() { return textures; }

    public String textureForVariant(int variant) {
        if (textures.isEmpty()) {
            return "entity/spectrobe/" + key + "/" + key + "_color_0";
        }
        int idx = clamp(variant, 0, textures.size() - 1);
        return textures.get(idx);
    }

    public boolean canEvolve() {
        return evolution.canEvolve();
    }

    private static String normalizeKey(String key) {
        if (key == null) return "";
        return key.trim().toLowerCase(Locale.ROOT);
    }

    private static List<String> normalizeTextures(String key, List<String> in) {
        List<String> out = new ArrayList<>();
        if (in != null) {
            for (String s : in) {
                if (s != null && !s.isBlank()) {
                    out.add(s.trim());
                }
            }
        }

        // Queremos siempre 3 texturas como fallback para variantes 0,1,2
        if (out.isEmpty()) {
            out.add("entity/spectrobe/" + key + "/" + key + "_color_0");
            out.add("entity/spectrobe/" + key + "/" + key + "_color_1");
            out.add("entity/spectrobe/" + key + "/" + key + "_color_2");
        } else if (out.size() == 1) {
            out.add(out.get(0));
            out.add(out.get(0));
        } else if (out.size() == 2) {
            out.add(out.get(0));
        } else if (out.size() > 3) {
            out = new ArrayList<>(out.subList(0, 3));
        }

        return List.copyOf(out);
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public static final class EvolutionData {
        private final String to;
        private final int mineralsRequired;

        public EvolutionData(String to, int mineralsRequired) {
            this.to = normalizeKey(to);
            this.mineralsRequired = Math.max(0, mineralsRequired);
        }

        public String to() { return to; }
        public int mineralsRequired() { return mineralsRequired; }

        public boolean canEvolve() {
            return !to.isBlank();
        }

        public static EvolutionData none() {
            return new EvolutionData("", 0);
        }
    }
}