package com.jvprz.spectrobesreforged.common.feature.krawl;

import com.jvprz.spectrobesreforged.common.content.entity.KrawlEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class KrawlMeleeAttackGoal extends MeleeAttackGoal {

    private final KrawlEntity krawl;

    public KrawlMeleeAttackGoal(KrawlEntity krawl, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(krawl, speedModifier, followingTargetEvenIfNotSeen);
        this.krawl = krawl;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.canPerformAttack(target) && !krawl.isAttackingAnimation()) {
            this.resetAttackCooldown();
            this.krawl.swing(InteractionHand.MAIN_HAND);
            this.krawl.startDelayedAttack(target);
        }
    }
}