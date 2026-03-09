// src/main/java/com/jvprz/spectrobesreforged/common/content/entity/SpectrobeEntity.java
package com.jvprz.spectrobesreforged.common.content.entity;

import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeStage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class SpectrobeEntity extends PathfinderMob implements GeoEntity {

    /* =========================
       Synced Data (CLIENT <-> SERVER)
       ========================= */

    private static final EntityDataAccessor<String> SPECIES_KEY =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<String> STAGE =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> RANGE_PAUSED =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> RANGE_PAUSE_TICKS =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.INT);

    /* =========================
       Animaciones
       ========================= */

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /* =========================
       Owner
       ========================= */

    private UUID ownerUUID;

    /* =========================
       Pause / range circle state
       ========================= */

    private double pausedX;
    private double pausedY;
    private double pausedZ;

    public static final int DEFAULT_RANGE_PAUSE_TICKS = 60; // 3 segundos

    public void setOwner(Player player) {
        this.ownerUUID = player == null ? null : player.getUUID();
    }

    public Optional<Player> getOwner() {
        if (ownerUUID == null) return Optional.empty();
        return Optional.ofNullable(level().getPlayerByUUID(ownerUUID));
    }

    /* =========================
       Constructor
       ========================= */

    public SpectrobeEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    protected String getDefaultSpeciesKey() {
        return "unknown";
    }

    protected SpectrobeStage getDefaultStage() {
        return SpectrobeStage.CHILD;
    }

    protected int getTextureVariantCount() {
        return 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPECIES_KEY, safeKey(getDefaultSpeciesKey()));
        builder.define(TEXTURE_VARIANT, 0);
        builder.define(STAGE, getDefaultStage().name());
        builder.define(RANGE_PAUSED, false);
        builder.define(RANGE_PAUSE_TICKS, 0);
    }

    /* =========================
       API de species / textura / stage
       ========================= */

    public String getSpeciesKey() {
        return this.entityData.get(SPECIES_KEY);
    }

    public void setSpeciesKey(String key) {
        this.entityData.set(SPECIES_KEY, safeKey(key));
    }

    public int getTextureVariant() {
        return this.entityData.get(TEXTURE_VARIANT);
    }

    public void setTextureVariant(int variant) {
        int maxIdx = Math.max(0, getTextureVariantCount() - 1);
        this.entityData.set(TEXTURE_VARIANT, Mth.clamp(variant, 0, maxIdx));
    }

    public SpectrobeStage getStage() {
        String raw = this.entityData.get(STAGE);
        try {
            return SpectrobeStage.valueOf(raw);
        } catch (Exception ignored) {
            return SpectrobeStage.CHILD;
        }
    }

    public void setStage(SpectrobeStage stage) {
        this.entityData.set(STAGE, (stage == null ? SpectrobeStage.CHILD : stage).name());
    }

    public boolean isRangePaused() {
        return this.entityData.get(RANGE_PAUSED);
    }

    public void setRangePaused(boolean paused) {
        this.entityData.set(RANGE_PAUSED, paused);
    }

    public int getRangePauseTicks() {
        return this.entityData.get(RANGE_PAUSE_TICKS);
    }

    public void setRangePauseTicks(int ticks) {
        this.entityData.set(RANGE_PAUSE_TICKS, Math.max(0, ticks));
    }

    public boolean shouldRenderRangeCircle() {
        return isRangePaused() && getRangePauseTicks() > 0;
    }

    public Vec3 getPausedCenter() {
        return new Vec3(pausedX, pausedY, pausedZ);
    }

    public void triggerRangePause() {
        triggerRangePause(DEFAULT_RANGE_PAUSE_TICKS);
    }

    public void triggerRangePause(int ticks) {
        this.pausedX = getX();
        this.pausedY = getY();
        this.pausedZ = getZ();

        setRangePaused(true);
        setRangePauseTicks(ticks);

        getNavigation().stop();
        setDeltaMovement(Vec3.ZERO);
    }

    public void stopRangePause() {
        setRangePaused(false);
        setRangePauseTicks(0);
    }

    private static String safeKey(String key) {
        if (key == null) return "unknown";
        String k = key.trim().toLowerCase(Locale.ROOT);
        return k.isEmpty() ? "unknown" : k;
    }

    /* =========================
       Goals (IA) por stage
       ========================= */

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        configureStageGoals(getStage());
    }

    protected void configureStageGoals(SpectrobeStage stage) {
        if (stage == null) stage = SpectrobeStage.CHILD;

        switch (stage) {
            case CHILD -> configureChildGoals();
            case ADULT -> configureAdultGoals();
            case EVOLVED -> configureEvolvedGoals();
        }
    }

    protected void configureChildGoals() {
        this.goalSelector.addGoal(2, new HoldRangePauseGoal(this));
        this.goalSelector.addGoal(3, new FollowOwnerSideGoal(this, 1.2));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    protected void configureAdultGoals() {
        configureChildGoals();
    }

    protected void configureEvolvedGoals() {
        configureAdultGoals();
    }

    public void refreshGoalsForCurrentStage() {
        this.goalSelector.getAvailableGoals().clear();
        this.targetSelector.getAvailableGoals().clear();
        registerGoals();
    }

    /* =========================
       Tick
       ========================= */

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && isRangePaused()) {
            int ticks = getRangePauseTicks();

            if (ticks > 0) {
                setRangePauseTicks(ticks - 1);
                getNavigation().stop();
                setDeltaMovement(Vec3.ZERO);
            }

            if (getRangePauseTicks() <= 0) {
                stopRangePause();
            }
        }
    }

    /* =========================
       Animaciones
       ========================= */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0, state -> {
            if (state.isMoving()) {
                state.setAnimation(WALK);
            } else {
                state.setAnimation(IDLE);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    /* =========================
       Guardado NBT
       ========================= */

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }

        tag.putString("SpeciesKey", getSpeciesKey());
        tag.putInt("TextureVariant", getTextureVariant());
        tag.putString("Stage", getStage().name());

        tag.putBoolean("RangePaused", isRangePaused());
        tag.putInt("RangePauseTicks", getRangePauseTicks());
        tag.putDouble("PausedX", pausedX);
        tag.putDouble("PausedY", pausedY);
        tag.putDouble("PausedZ", pausedZ);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.hasUUID("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }

        if (tag.contains("SpeciesKey")) {
            setSpeciesKey(tag.getString("SpeciesKey"));
        }

        if (tag.contains("TextureVariant")) {
            setTextureVariant(tag.getInt("TextureVariant"));
        }

        if (tag.contains("Stage")) {
            try {
                setStage(SpectrobeStage.valueOf(tag.getString("Stage")));
            } catch (Exception ignored) {
                setStage(SpectrobeStage.CHILD);
            }
        }

        if (tag.contains("RangePaused")) {
            setRangePaused(tag.getBoolean("RangePaused"));
        }

        if (tag.contains("RangePauseTicks")) {
            setRangePauseTicks(tag.getInt("RangePauseTicks"));
        }

        if (tag.contains("PausedX")) pausedX = tag.getDouble("PausedX");
        if (tag.contains("PausedY")) pausedY = tag.getDouble("PausedY");
        if (tag.contains("PausedZ")) pausedZ = tag.getDouble("PausedZ");
    }

    /* =========================
       Spawn defaults
       ========================= */

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType reason,
                                        SpawnGroupData spawnData) {

        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);

        if (!this.level().isClientSide) {
            if ("unknown".equals(getSpeciesKey())) {
                setSpeciesKey(getDefaultSpeciesKey());
            }

            if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND) {
                int count = Math.max(1, getTextureVariantCount());
                setTextureVariant(this.random.nextInt(count));
            }

            if (ownerUUID == null && (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND)) {
                Player nearest = this.level().getNearestPlayer(this, 6.0);
                if (nearest != null) {
                    setOwner(nearest);
                }
            }
        }

        return data;
    }

    /* =========================
       Goals personalizados
       ========================= */

    private static class HoldRangePauseGoal extends Goal {

        private final SpectrobeEntity mob;

        public HoldRangePauseGoal(SpectrobeEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return mob.isRangePaused();
        }

        @Override
        public boolean canContinueToUse() {
            return mob.isRangePaused() && mob.getRangePauseTicks() > 0;
        }

        @Override
        public void start() {
            mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            mob.getNavigation().stop();
            mob.setDeltaMovement(Vec3.ZERO);

            Vec3 center = mob.getPausedCenter();
            if (mob.distanceToSqr(center.x, center.y, center.z) > 0.09D) {
                mob.teleportTo(center.x, center.y, center.z);
            }
        }

        @Override
        public void stop() {
            mob.getNavigation().stop();
        }
    }

    private static class FollowOwnerSideGoal extends Goal {

        private final SpectrobeEntity mob;
        private final double speed;
        private Player owner;
        private int recalcTime;

        public FollowOwnerSideGoal(SpectrobeEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            if (mob.isRangePaused()) return false;

            this.owner = mob.getOwner().orElse(null);
            if (owner == null) return false;
            return mob.distanceTo(owner) > 3.0F;
        }

        @Override
        public boolean canContinueToUse() {
            if (mob.isRangePaused()) return false;
            if (owner == null || !owner.isAlive()) return false;
            return mob.distanceTo(owner) > 2.0F;
        }

        @Override
        public void tick() {
            if (owner == null || mob.isRangePaused()) return;

            if (mob.distanceTo(owner) > 20.0F) {
                mob.teleportTo(owner.getX(), owner.getY(), owner.getZ());
                return;
            }

            if (--recalcTime <= 0) {
                recalcTime = 10;

                Vec3 look = owner.getLookAngle().normalize();
                Vec3 side = new Vec3(-look.z, 0, look.x);

                Vec3 targetPos = owner.position().add(side.scale(1.5));

                mob.getNavigation().moveTo(
                        targetPos.x,
                        targetPos.y,
                        targetPos.z,
                        speed
                );
            }
        }
    }
}