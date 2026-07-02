package com.jvprz.spectrobesreforged.common.feature.krawl;

public record KrawlSpawnData(
        boolean enabled,
        int weight,
        int minGroup,
        int maxGroup,
        int minLightLevel,
        int maxLightLevel
) {
}