package com.jvprz.spectrobesreforged.common.feature.incubator.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IncubatorMusicTest {
    @Test
    void musicRemainsWithinDesignedBandsAndWeakensWithDistance() {
        for (long tick = 0; tick < 24000; tick += 17) {
            int near = IncubatorMusic.frequency(tick, 0, 16, 16);
            int far = IncubatorMusic.frequency(tick, 16, 16, 16);
            assertTrue(near >= 12 && near <= 16);
            assertTrue(far >= 4 && far <= 5);
            assertTrue(far <= near);
        }
    }
}
