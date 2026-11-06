package com.jvprz.spectrobesreforged.common.feature.audio;

/** Relative mix gains applied locally before Minecraft's own category controls. */
public final class ModSoundBalance {
    private ModSoundBalance() {}
    public static float gain(String event) {
        return switch(event) {
            case "scanner_found" -> .24F;
            case "scanner_open" -> .60F;
            case "awakening_cue" -> .45F;
            case "battle_start", "battle_end" -> .65F;
            case "entity.krawl.idle", "entity.krawl.hurt", "entity.krawl.death" -> .85F;
            case "entity.krawl.attack" -> .90F;
            default -> 1F;
        };
    }
}
