package com.jvprz.spectrobesreforged.common.feature.krawl;

public record KrawlDefinition(
        int id,
        String key,
        String name,
        KrawlElement element,
        KrawlRank rank,
        KrawlStats stats,
        KrawlAiData ai,
        KrawlSpawnData spawn,
        KrawlCombatData combat,
        KrawlEggData egg
) {
}