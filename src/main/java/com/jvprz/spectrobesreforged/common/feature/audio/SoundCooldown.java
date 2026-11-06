package com.jvprz.spectrobesreforged.common.feature.audio;

/** Limits clusters of one-shot sounds; a clock reset starts a fresh window. */
public final class SoundCooldown {
    private final int interval;
    private long last=Long.MIN_VALUE;
    public SoundCooldown(int interval) { this.interval=interval; }
    public boolean accept(long now) {
        if(last!=Long.MIN_VALUE && now>=last && now-last<interval)return false;
        last=now;
        return true;
    }
    public void reset() { last=Long.MIN_VALUE; }
}
