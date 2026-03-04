package com.jvprz.spectrobesreforged.common.content.entity;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;

public class KomainuEntity extends PathfinderMob implements GeoEntity {

    /* =========================
       Animaciones
       ========================= */

    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("idle");

    private static final RawAnimation WALK =
            RawAnimation.begin().thenLoop("walk");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /* =========================
       Owner
       ========================= */

    private UUID ownerUUID;

    public void setOwner(Player player) {
        this.ownerUUID = player.getUUID();
    }

    public Optional<Player> getOwner() {
        if (ownerUUID == null) return Optional.empty();
        return Optional.ofNullable(level().getPlayerByUUID(ownerUUID));
    }

    /* =========================
       Constructor
       ========================= */

    public KomainuEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    /* =========================
       Goals
       ========================= */

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerSideGoal(this, 1.2));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }
    }

    /* =========================
       Auto-asignar owner al spawnear
       ========================= */

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType reason,
                                        SpawnGroupData spawnData) {

        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);

        if (!this.level().isClientSide && ownerUUID == null &&
                (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND)) {

            Player nearest = this.level().getNearestPlayer(this, 6.0);
            if (nearest != null) setOwner(nearest);
        }

        return data;
    }

    /* =========================
       Goal personalizado
       ========================= */

    private static class FollowOwnerSideGoal extends Goal {

        private final KomainuEntity mob;
        private final double speed;
        private Player owner;
        private int recalcTime;

        public FollowOwnerSideGoal(KomainuEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            this.owner = mob.getOwner().orElse(null);
            if (owner == null) return false;
            return mob.distanceTo(owner) > 3.0F;
        }

        @Override
        public boolean canContinueToUse() {
            if (owner == null || !owner.isAlive()) return false;
            return mob.distanceTo(owner) > 2.0F;
        }

        @Override
        public void tick() {
            if (owner == null) return;

            // Si está muy lejos, teleporta
            if (mob.distanceTo(owner) > 20.0F) {
                mob.teleportTo(owner.getX(), owner.getY(), owner.getZ());
                return;
            }

            if (--recalcTime <= 0) {
                recalcTime = 10;

                // Posición lateral al jugador
                Vec3 look = owner.getLookAngle().normalize();
                Vec3 side = new Vec3(-look.z, 0, look.x); // perpendicular

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