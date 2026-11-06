package com.jvprz.spectrobesreforged.common.feature.audio;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SoundCooldownTest {
    @Test void denseTargetsCannotStackPingsButLaterSweepsStillSound() {
        var gate=new SoundCooldown(6);
        assertTrue(gate.accept(0));
        for(int i=0;i<20;i++)assertFalse(gate.accept(0));
        assertFalse(gate.accept(5));
        assertTrue(gate.accept(6));
        assertFalse(gate.accept(7));
        assertTrue(gate.accept(12));
    }
    @Test void reconnectAndClockResetCannotSilenceANewWorld() {
        var gate=new SoundCooldown(6);
        assertTrue(gate.accept(1000));
        assertTrue(gate.accept(0));
        assertFalse(gate.accept(0));
        gate.reset();
        assertTrue(gate.accept(0));
    }
}
