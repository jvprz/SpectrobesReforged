package com.jvprz.spectrobesreforged.common.feature.spectrobe.ai;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompanionTemperamentTest {
    @Test void pursuitSlowsNearTheOwnerAndIsBoundedForEverySpecies() {
        for(String key:new String[]{"komainu","spiko","vilar","segu","inkana","shakin","zoza","unknown"}) {
            var profile=CompanionTemperament.of(key);
            assertTrue(profile.followSpeed(3)<profile.followSpeed(9),key);
            assertEquals(profile.followSpeed(0),profile.followSpeed(3),.00001);
            assertTrue(profile.followSpeed(100)<=1.45,key);
            assertTrue(profile.followSpeed(3)>.5,key);
        }
    }
    @Test void calmerSpeciesReallyHaveLongerPausesAndSlowerPursuit() {
        var active=CompanionTemperament.of("komainu");var calm=CompanionTemperament.of("segu");
        assertTrue(calm.curiosityInterval()>active.curiosityInterval());
        assertTrue(calm.strollInterval()>active.strollInterval());
        assertTrue(calm.followSpeed(7)<active.followSpeed(7));
    }
}
