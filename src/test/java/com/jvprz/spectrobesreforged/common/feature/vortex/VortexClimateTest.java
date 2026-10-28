package com.jvprz.spectrobesreforged.common.feature.vortex;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VortexClimateTest {
    @Test void untaggedTemperateBiomesNoLongerDefaultToBlue() {
        assertEquals("viblova", VortexRules.krawl(false,false,false,0.8F));
        assertEquals("viblova", VortexRules.krawl(false,false,false,0.2F));
        assertEquals("blova", VortexRules.krawl(false,false,false,0.199F));
        assertEquals("blovant", VortexRules.krawl(false,false,false,1.0F));
        assertEquals("blovant", VortexRules.krawl(false,false,false,2.0F));
    }
    @Test void explicitTagsOverrideTemperatureWithDeterministicPriority() {
        assertEquals("blovant", VortexRules.krawl(true,true,true,-0.5F));
        assertEquals("viblova", VortexRules.krawl(false,true,true,2.0F));
        assertEquals("blova", VortexRules.krawl(false,false,true,0.25F));
    }
    @Test void allClimateChoicesReferToSupportedKrawlAndTheirMatchingElement() {
        assertTrue(VortexRules.supported(VortexRules.krawl(true,false,false,0)));
        assertEquals("corona",VortexRules.element("blovant"));
        assertEquals("aurora",VortexRules.element("viblova"));
        assertEquals("flash",VortexRules.element("blova"));
        assertFalse(VortexRules.supported(""));
        assertFalse(VortexRules.supported("missing"));
    }
    @Test void naturallySpawnedVorticesCanAcquireTheirDiscoveringPlayer() {
        assertTrue(VortexRules.MAX_SPAWN_DISTANCE + 1 < VortexRules.DETECTION_RADIUS,
                "A discovered vortex must detect its player even after rounding to block centers");
        assertTrue(VortexRules.MIN_SPAWN_DISTANCE > VortexRules.PLAYER_EXCLUSION_RADIUS);
        assertTrue(VortexRules.RETENTION_RADIUS > VortexRules.DETECTION_RADIUS);
    }
}
