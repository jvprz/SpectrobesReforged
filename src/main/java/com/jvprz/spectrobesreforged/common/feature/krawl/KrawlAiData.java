package com.jvprz.spectrobesreforged.common.feature.krawl;

public record KrawlAiData(
        String type,
        boolean aggressive,
        boolean targetPlayers,
        boolean targetSpectrobes,
        boolean callsForHelp,
        boolean breakBlocks
) {
}