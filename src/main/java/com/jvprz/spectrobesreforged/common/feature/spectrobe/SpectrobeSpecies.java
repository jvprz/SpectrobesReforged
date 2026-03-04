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
    private final SpectrobeStats baseStats;
    private final List<String> textures;

    public SpectrobeSpecies(
            int id,
            String key,
            SpectrobeType type,
            SpectrobeStage initialStage,
            int range,
            SpectrobeStats baseStats,
            List<String> textures
    ) {
        this.id = id;
        this.key = normalizeKey(key);
        this.type = type;
        this.initialStage = initialStage;
        this.range = clamp(range, 1, 10); // ajusta si tu juego usa otro rango
        this.baseStats = baseStats;
        this.textures = normalizeTextures(this.key, textures);
    }

    public int id() { return id; }
    public String key() { return key; }
    public SpectrobeType type() { return type; }
    public SpectrobeStage initialStage() { return initialStage; }
    public int range() { return range; }
    public SpectrobeStats baseStats() { return baseStats; }
    public List<String> textures() { return textures; }

    public String textureForVariant(int variant) {
        if (textures.isEmpty()) return "entity/spectrobe/" + key + "_0";
        int idx = clamp(variant, 0, textures.size() - 1);
        return textures.get(idx);
    }

    private static String normalizeKey(String key) {
        if (key == null) return "";
        return key.trim().toLowerCase(Locale.ROOT);
    }

    private static List<String> normalizeTextures(String key, List<String> in) {
        List<String> out = new ArrayList<>();
        if (in != null) {
            for (String s : in) {
                if (s != null && !s.isBlank()) out.add(s.trim());
            }
        }

        // Queremos siempre 3 texturas (o lo más cercano posible)
        if (out.isEmpty()) {
            out.add("entity/spectrobe/" + key + "_0");
            out.add("entity/spectrobe/" + key + "_1");
            out.add("entity/spectrobe/" + key + "_2");
        } else if (out.size() == 1) {
            out.add(out.get(0));
            out.add(out.get(0));
        } else if (out.size() == 2) {
            out.add(out.get(0)); // rellena la tercera
        } else if (out.size() > 3) {
            out = out.subList(0, 3);
        }

        return List.copyOf(out);
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}