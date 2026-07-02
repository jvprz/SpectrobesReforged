package com.jvprz.spectrobesreforged.common.content.entity;

import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlDefinition;
import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlRegistry;
import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlStats;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public class KrawlEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private String krawlKey;
    private KrawlDefinition definition;

    public KrawlEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.20D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
    }

    public KrawlDefinition getDefinition() {
        if (definition == null && krawlKey != null) {
            definition = KrawlRegistry.getByKey(krawlKey);
        }

        if (definition == null) {
            definition = KrawlRegistry.all().stream().findFirst().orElse(null);

            if (definition != null) {
                krawlKey = definition.key();
            }
        }

        return definition;
    }

    public String getKrawlKey() {
        return krawlKey;
    }

    public void setDefinition(KrawlDefinition definition) {
        if (definition == null) {
            return;
        }

        this.definition = definition;
        this.krawlKey = definition.key();

        applyDefinitionStats();
    }

    private void applyDefinitionStats() {
        KrawlDefinition currentDefinition = getDefinition();

        if (currentDefinition == null) {
            return;
        }

        KrawlStats stats = currentDefinition.stats();

        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH))
                .setBaseValue(stats.hp());

        Objects.requireNonNull(getAttribute(Attributes.ATTACK_DAMAGE))
                .setBaseValue(stats.attack());

        Objects.requireNonNull(getAttribute(Attributes.ARMOR))
                .setBaseValue(stats.defense());

        Objects.requireNonNull(getAttribute(Attributes.MOVEMENT_SPEED))
                .setBaseValue(stats.moveSpeed());

        Objects.requireNonNull(getAttribute(Attributes.FOLLOW_RANGE))
                .setBaseValue(stats.followRange());

        Objects.requireNonNull(getAttribute(Attributes.KNOCKBACK_RESISTANCE))
                .setBaseValue(stats.knockbackResistance());

        setHealth(getMaxHealth());
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        applyDefinitionStats();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (krawlKey != null) {
            tag.putString("KrawlKey", krawlKey);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("KrawlKey")) {
            this.krawlKey = tag.getString("KrawlKey");
            this.definition = KrawlRegistry.getByKey(krawlKey);
        }

        applyDefinitionStats();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Animaciones próximamente.
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}