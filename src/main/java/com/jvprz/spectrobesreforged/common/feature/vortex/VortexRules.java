package com.jvprz.spectrobesreforged.common.feature.vortex;

/** Shared encounter balance and climate fallback, independent of rendering and world loading. */
public final class VortexRules {
    private VortexRules() {}
    public static final int DISCOVERY_INTERVAL = 400;
    public static final int DISCOVERY_CHANCE = 2;
    public static final int SITE_ATTEMPTS = 6;
    public static final int MIN_SPAWN_DISTANCE = 32;
    public static final int MAX_SPAWN_DISTANCE = 48;
    public static final int PLAYER_EXCLUSION_RADIUS = 24;
    public static final int DISCOVERY_COOLDOWN = 1200;
    public static final int DETECTION_RADIUS = 64;
    public static final int RETENTION_RADIUS = 80;
    public static final int NEARBY_VORTEX_RADIUS = 96;
    public static final double CHASE_SPEED = 0.25;
    public static final String NEXT_DISCOVERY = "SpectrobesNextVortexDiscovery";

    public static String krawl(boolean hot, boolean temperate, boolean cold, float temperature) {
        if (hot) return "blovant";
        if (temperate) return "viblova";
        if (cold) return "blova";
        return temperature >= 1.0F ? "blovant" : temperature < 0.2F ? "blova" : "viblova";
    }

    public static boolean supported(String key) {
        return "blovant".equals(key) || "viblova".equals(key) || "blova".equals(key);
    }

    public static String element(String key) {
        return "blovant".equals(key) ? "corona" : "viblova".equals(key) ? "aurora" : "flash";
    }
}
