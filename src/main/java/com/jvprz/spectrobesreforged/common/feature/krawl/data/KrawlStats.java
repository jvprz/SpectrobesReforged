package com.jvprz.spectrobesreforged.common.feature.krawl.data;

public record KrawlStats(
        int hp,
        int attack,
        int defense,
        double moveSpeed,
        double followRange,
        int attackSpeed,
        double knockbackResistance
) {
}