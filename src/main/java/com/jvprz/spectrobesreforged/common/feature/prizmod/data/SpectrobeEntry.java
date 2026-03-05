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
        int hpCur,
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
            Codec.INT.optionalFieldOf("hpCur", -1).forGetter(SpectrobeEntry::hpCur),
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

        if (hp < 0) hp = 0;

        // Si viene “sin hpCur” en saves antiguos => full vida
        if (hpCur < 0) hpCur = hp;

        // Clamp
        if (hpCur < 0) hpCur = 0;
        if (hpCur > hp) hpCur = hp;
    }

    public boolean isChild() {
        return "CHILD".equals(stage);
    }

    public boolean isDead() {
        return hpCur <= 0;
    }

    public int total() {
        return hp + atk + def;
    }

    public SpectrobeEntry withHpCur(int newHpCur) {
        return new SpectrobeEntry(id, species, color, stage, level, hp, newHpCur, atk, def);
    }

    public SpectrobeEntry healFull() {
        return withHpCur(hp);
    }

    public SpectrobeEntry withColor(int newColor) {
        return new SpectrobeEntry(id, species, newColor, stage, level, hp, hpCur, atk, def);
    }
}