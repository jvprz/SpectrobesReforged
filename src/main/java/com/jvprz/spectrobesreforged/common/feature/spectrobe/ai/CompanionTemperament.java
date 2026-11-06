package com.jvprz.spectrobesreforged.common.feature.spectrobe.ai;

/** Small behavioral differences; no species stats or saved collection values change. */
public record CompanionTemperament(double pace, int curiosityInterval, int strollInterval) {
    public static CompanionTemperament of(String species) {
        return switch(species) {
            case "komainu" -> new CompanionTemperament(1.08,150,110);
            case "spiko" -> new CompanionTemperament(.90,250,200);
            case "segu" -> new CompanionTemperament(.82,290,250);
            case "vilar" -> new CompanionTemperament(1.0,190,150);
            case "inkana" -> new CompanionTemperament(.94,230,190);
            case "shakin", "zoza" -> new CompanionTemperament(1.04,180,140);
            default -> new CompanionTemperament(1.0,220,170);
        };
    }
    public double followSpeed(double distance) {
        return Math.min(1.45, (.75 + Math.clamp((distance-3)/6,0,1)*.6)*pace);
    }
}
