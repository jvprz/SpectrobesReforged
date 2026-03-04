package com.jvprz.spectrobesreforged.common.feature.spectrobe;

import java.util.Locale;

public enum SpectrobeType {
    CORONA,
    AURORA,
    FLASH;

    public static SpectrobeType fromString(String s) {
        if (s == null) throw new IllegalArgumentException("type is null");
        return switch (s.trim().toLowerCase(Locale.ROOT)) {
            case "corona" -> CORONA;
            case "aurora" -> AURORA;
            case "flash" -> FLASH;
            default -> throw new IllegalArgumentException("Unknown SpectrobeType: " + s);
        };
    }
}