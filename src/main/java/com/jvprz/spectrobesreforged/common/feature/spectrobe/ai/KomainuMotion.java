package com.jvprz.spectrobesreforged.common.feature.spectrobe.ai;

/** Komainu-only gait hysteresis prevents walk/run flicker at the speed boundary. */
public final class KomainuMotion {
    private KomainuMotion() {}
    public static boolean running(boolean wasRunning,float limbAmount) {return limbAmount>(wasRunning?.60F:.80F);}
}
