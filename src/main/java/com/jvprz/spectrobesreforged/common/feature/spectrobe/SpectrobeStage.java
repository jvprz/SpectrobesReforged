package com.jvprz.spectrobesreforged.common.feature.spectrobe;

import java.util.Locale;

public enum SpectrobeStage {
    CHILD,
    ADULT,
    EVOLVED;

    public static SpectrobeStage fromString(String s) {
        if (s == null) throw new IllegalArgumentException("stage is null");
        return switch (s.trim().toLowerCase(Locale.ROOT)) {
            case "child" -> CHILD;
            case "adult" -> ADULT;
            case "evolved" -> EVOLVED;
            default -> throw new IllegalArgumentException("Unknown SpectrobeStage: " + s);
        };
    }
}