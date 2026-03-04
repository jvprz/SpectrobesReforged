package com.jvprz.spectrobesreforged.common.feature.spectrobe;

public record SpectrobeStats(int hp, int attack, int defense) {
    public int total() {
        return hp + attack + defense;
    }
}