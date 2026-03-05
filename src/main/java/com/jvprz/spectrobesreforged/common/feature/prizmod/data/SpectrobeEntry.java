// src/main/java/com/jvprz/spectrobesreforged/common/feature/prizmod/data/SpectrobeEntry.java
package com.jvprz.spectrobesreforged.common.feature.prizmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Locale;
import java.util.UUID;

public record SpectrobeEntry(
        UUID id,
        String species,
        int color,
        String stage, // "CHILD" | "ADULT" | "EVOLVED"
        int level,
        int hp,
        int atk,
        int def
) {

    public static final Codec<SpectrobeEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("id").forGetter(SpectrobeEntry::id),
            Codec.STRING.fieldOf("species").forGetter(SpectrobeEntry::species),
            Codec.INT.optionalFieldOf("color", 0).forGetter(SpectrobeEntry::color),
            Codec.STRING.optionalFieldOf("stage", "CHILD").forGetter(SpectrobeEntry::stage),
            Codec.INT.optionalFieldOf("level", 1).forGetter(SpectrobeEntry::level),
            Codec.INT.optionalFieldOf("hp", 0).forGetter(SpectrobeEntry::hp),
            Codec.INT.optionalFieldOf("atk", 0).forGetter(SpectrobeEntry::atk),
            Codec.INT.optionalFieldOf("def", 0).forGetter(SpectrobeEntry::def)
    ).apply(inst, SpectrobeEntry::new));

    public SpectrobeEntry {
        species = species == null ? "" : species.trim().toLowerCase(Locale.ROOT);

        if (color < 0) color = 0;
        if (color > 2) color = 2;

        if (stage == null || stage.isBlank()) stage = "CHILD";
        stage = stage.trim().toUpperCase(Locale.ROOT);

        if (level < 1) level = 1;
    }

    public boolean isChild() {
        return "CHILD".equals(stage);
    }

    public int total() {
        return hp + atk + def;
    }

    // =========================
    // Immutable "with" helpers
    // =========================

    public SpectrobeEntry withColor(int newColor) {
        return new SpectrobeEntry(
                this.id,
                this.species,
                newColor,
                this.stage,
                this.level,
                this.hp,
                this.atk,
                this.def
        );
    }

    public SpectrobeEntry withStage(String newStage) {
        return new SpectrobeEntry(
                this.id,
                this.species,
                this.color,
                newStage,
                this.level,
                this.hp,
                this.atk,
                this.def
        );
    }

    public SpectrobeEntry withLevel(int newLevel) {
        return new SpectrobeEntry(
                this.id,
                this.species,
                this.color,
                this.stage,
                newLevel,
                this.hp,
                this.atk,
                this.def
        );
    }

    public SpectrobeEntry withStats(int newHp, int newAtk, int newDef) {
        return new SpectrobeEntry(
                this.id,
                this.species,
                this.color,
                this.stage,
                this.level,
                newHp,
                newAtk,
                newDef
        );
    }
}