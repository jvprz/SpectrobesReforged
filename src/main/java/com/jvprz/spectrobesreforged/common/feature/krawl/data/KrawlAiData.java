package com.jvprz.spectrobesreforged.common.feature.krawl.data;

public record KrawlAiData(
        String type,
        boolean aggressive,
        boolean targetPlayers,
        boolean targetSpectrobes,
        boolean callsForHelp,
        boolean breakBlocks
) {
}