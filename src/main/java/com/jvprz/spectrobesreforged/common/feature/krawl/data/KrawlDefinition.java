package com.jvprz.spectrobesreforged.common.feature.krawl.data;

public record KrawlDefinition(
        int id,
        String key,
        String name,
        KrawlElement element,
        KrawlTier rank,
        KrawlEntityData entity,
        KrawlStats stats,
        KrawlAiData ai,
        KrawlSpawnData spawn,
        KrawlCombatData combat,
        KrawlEggData egg
) {
}