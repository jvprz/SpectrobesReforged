package com.jvprz.spectrobesreforged.common.feature.spectrobe;

public final class SpectrobeStats {

    private final StatGrowth hp;
    private final StatGrowth attack;
    private final StatGrowth defense;

    private final int moveSpeed;
    private final int chargeSpeed;

    private final SupportStats support;

    public SpectrobeStats(
            StatGrowth hp,
            StatGrowth attack,
            StatGrowth defense,
            int moveSpeed,
            int chargeSpeed,
            SupportStats support
    ) {
        this.hp = hp != null ? hp : StatGrowth.zero();
        this.attack = attack != null ? attack : StatGrowth.zero();
        this.defense = defense != null ? defense : StatGrowth.zero();
        this.moveSpeed = moveSpeed;
        this.chargeSpeed = chargeSpeed;
        this.support = support != null ? support : SupportStats.zero();
    }

    public StatGrowth hp() { return hp; }
    public StatGrowth attack() { return attack; }
    public StatGrowth defense() { return defense; }

    public int moveSpeed() { return moveSpeed; }
    public int chargeSpeed() { return chargeSpeed; }

    public SupportStats support() { return support; }

    public static SpectrobeStats empty() {
        return new SpectrobeStats(
                StatGrowth.zero(),
                StatGrowth.zero(),
                StatGrowth.zero(),
                0,
                0,
                SupportStats.zero()
        );
    }
}