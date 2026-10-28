package com.jvprz.spectrobesreforged.common.feature.excavation;

import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Masks are generated from the existing fossil sprites, and work on dedicated servers. */
public final class ExcavationMasks {
    private static final Map<String, boolean[]> MASKS = load();
    private ExcavationMasks() {}
    private static Map<String, boolean[]> load() {
        var result = new HashMap<String, boolean[]>();
        try (var stream = ExcavationMasks.class.getResourceAsStream("/data/spectrobesreforged/excavation_masks.json")) {
            if (stream == null) throw new IllegalStateException("Missing excavation masks");
            var json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            json.entrySet().forEach(entry -> {
                String bits = entry.getValue().getAsString();
                if (bits.length() != ExcavationPuzzle.CELLS) throw new IllegalStateException("Invalid mask");
                boolean[] mask = new boolean[bits.length()];
                for (int i = 0; i < bits.length(); i++) mask[i] = bits.charAt(i) == '1';
                result.put(entry.getKey(), mask);
            });
        } catch (Exception e) { throw new IllegalStateException("Cannot load excavation masks", e); }
        return Map.copyOf(result);
    }
    public static boolean[] get(String species) {
        boolean[] mask = MASKS.get(species);
        return mask == null ? null : mask.clone();
    }
}
