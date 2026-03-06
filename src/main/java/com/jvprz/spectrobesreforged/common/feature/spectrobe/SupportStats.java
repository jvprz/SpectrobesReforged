package com.jvprz.spectrobesreforged.common.feature.spectrobe;

public final class SupportStats {

    private final int attack;
    private final int defense;
    private final int speed;
    private final int charge;

    public SupportStats(int attack, int defense, int speed, int charge) {
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.charge = charge;
    }

    public int attack() { return attack; }
    public int defense() { return defense; }
    public int speed() { return speed; }
    public int charge() { return charge; }

    public static SupportStats zero() {
        return new SupportStats(0,0,0,0);
    }
}