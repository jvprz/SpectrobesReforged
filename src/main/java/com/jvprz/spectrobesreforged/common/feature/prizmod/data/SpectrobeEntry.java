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
        int def,
        int mineralsFed,
        int mineralHpBonus,
        int mineralAtkBonus,
        int mineralDefBonus
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
            Codec.INT.optionalFieldOf("def", 0).forGetter(SpectrobeEntry::def),
            Codec.INT.optionalFieldOf("mineralsFed", 0).forGetter(SpectrobeEntry::mineralsFed),
            Codec.INT.optionalFieldOf("mineralHpBonus", 0).forGetter(SpectrobeEntry::mineralHpBonus),
            Codec.INT.optionalFieldOf("mineralAtkBonus", 0).forGetter(SpectrobeEntry::mineralAtkBonus),
            Codec.INT.optionalFieldOf("mineralDefBonus", 0).forGetter(SpectrobeEntry::mineralDefBonus)
    ).apply(inst, SpectrobeEntry::new));

    public SpectrobeEntry {
        species = species == null ? "" : species.trim().toLowerCase(Locale.ROOT);

        if (color < 0) color = 0;
        if (color > 2) color = 2;

        if (stage == null || stage.isBlank()) stage = "CHILD";
        stage = stage.trim().toUpperCase(Locale.ROOT);

        if (level < 1) level = 1;

        if (hp < 0) hp = 0;
        if (atk < 0) atk = 0;
        if (def < 0) def = 0;

        // Si viene “sin hpCur” en saves antiguos => full vida
        if (hpCur < 0) hpCur = hp;

        // Clamp
        if (hpCur < 0) hpCur = 0;
        if (hpCur > hp) hpCur = hp;

        if (mineralHpBonus < 0) mineralHpBonus = 0;
        if (mineralAtkBonus < 0) mineralAtkBonus = 0;
        if (mineralDefBonus < 0) mineralDefBonus = 0;
        if (mineralsFed < 0) mineralsFed = 0;
    }

    public boolean isChild() {
        return "CHILD".equals(stage);
    }

    public boolean isAdult() {
        return "ADULT".equals(stage);
    }

    public boolean isEvolved() {
        return "EVOLVED".equals(stage);
    }

    public boolean isDead() {
        return hpCur <= 0;
    }

    public int total() {
        return hp + atk + def;
    }

    public int totalMineralBonus() {
        return mineralHpBonus + mineralAtkBonus + mineralDefBonus;
    }

    public SpectrobeEntry withHpCur(int newHpCur) {
        return new SpectrobeEntry(
                id, species, color, stage, level,
                hp, newHpCur, atk, def,
                mineralsFed, mineralHpBonus, mineralAtkBonus, mineralDefBonus
        );
    }

    public SpectrobeEntry healFull() {
        return withHpCur(hp);
    }

    public SpectrobeEntry withColor(int newColor) {
        return new SpectrobeEntry(
                id, species, newColor, stage, level,
                hp, hpCur, atk, def,
                mineralsFed, mineralHpBonus, mineralAtkBonus, mineralDefBonus
        );
    }

    public SpectrobeEntry withStage(String newStage) {
        return new SpectrobeEntry(
                id, species, color, newStage, level,
                hp, hpCur, atk, def,
                mineralsFed, mineralHpBonus, mineralAtkBonus, mineralDefBonus
        );
    }

    public SpectrobeEntry withStats(int newHp, int newHpCur, int newAtk, int newDef) {
        return new SpectrobeEntry(
                id, species, color, stage, level,
                newHp, newHpCur, newAtk, newDef,
                mineralsFed, mineralHpBonus, mineralAtkBonus, mineralDefBonus
        );
    }

    public SpectrobeEntry withMineralsFed(int newMineralsFed) {
        return new SpectrobeEntry(
                id, species, color, stage, level,
                hp, hpCur, atk, def,
                newMineralsFed, mineralHpBonus, mineralAtkBonus, mineralDefBonus
        );
    }

    public SpectrobeEntry withMineralBonuses(int newHpBonus, int newAtkBonus, int newDefBonus) {
        return new SpectrobeEntry(
                id, species, color, stage, level,
                hp, hpCur, atk, def,
                mineralsFed, newHpBonus, newAtkBonus, newDefBonus
        );
    }

    public SpectrobeEntry applyMineralBonuses(int addHp, int addAtk, int addDef) {
        return new SpectrobeEntry(
                id, species, color, stage, level,
                hp, hpCur, atk, def,
                mineralsFed + 1,
                mineralHpBonus + Math.max(0, addHp),
                mineralAtkBonus + Math.max(0, addAtk),
                mineralDefBonus + Math.max(0, addDef)
        );
    }
}