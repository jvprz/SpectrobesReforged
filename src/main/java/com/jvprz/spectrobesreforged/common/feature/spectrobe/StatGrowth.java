package com.jvprz.spectrobesreforged.common.feature.spectrobe;

public final class StatGrowth {

    private final int base;
    private final int max;
    private final int increment;

    public StatGrowth(int base, int max, int increment) {
        this.base = base;
        this.max = max;
        this.increment = increment;
    }

    public int base() { return base; }
    public int max() { return max; }
    public int increment() { return increment; }

    public static StatGrowth zero() {
        return new StatGrowth(0,0,0);
    }
}