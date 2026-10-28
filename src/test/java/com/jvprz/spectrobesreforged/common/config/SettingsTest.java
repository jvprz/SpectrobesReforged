package com.jvprz.spectrobesreforged.common.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {
    @Test void feedbackModesKeepAtLeastOneUsefulCue(){
        assertTrue(ClientConfig.ScanFeedback.BOTH.visuals());assertTrue(ClientConfig.ScanFeedback.BOTH.sounds());
        assertTrue(ClientConfig.ScanFeedback.VISUAL.visuals());assertFalse(ClientConfig.ScanFeedback.VISUAL.sounds());
        assertFalse(ClientConfig.ScanFeedback.SOUND.visuals());assertTrue(ClientConfig.ScanFeedback.SOUND.sounds());
    }
    @Test void reducedParticlesRemainVisibleAndDensityOrderingIsConsistent(){
        for(int n:new int[]{3,6,15,24,36}){
            int low=ClientConfig.ParticleDensity.LOW.scale(n),medium=ClientConfig.ParticleDensity.MEDIUM.scale(n);
            assertTrue(low>0&&low<=medium&&medium<=n);assertEquals(n,ClientConfig.ParticleDensity.HIGH.scale(n));
        }
    }
    @Test void unloadedConfigsExposeSafeDefaultsForTestsAndEarlyLifecycle(){
        assertTrue(Config.naturalVortices());assertTrue(Config.discoveryTicks()>0);assertTrue(Config.vortexSpeed()>0);
        assertTrue(ClientConfig.scanVisuals());assertTrue(ClientConfig.scanSounds());
    }
}
