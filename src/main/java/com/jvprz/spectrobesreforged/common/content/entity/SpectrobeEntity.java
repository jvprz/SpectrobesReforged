// src/main/java/com/jvprz/spectrobesreforged/common/content/entity/SpectrobeEntity.java
package com.jvprz.spectrobesreforged.common.content.entity;

import java.util.Optional;
import java.util.UUID;

import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeStage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class SpectrobeEntity extends PathfinderMob implements GeoEntity {

    /* =========================
       Synced Data (CLIENT <-> SERVER)
       ========================= */

    private static final EntityDataAccessor<String> SPECIES_KEY =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<String> STAGE =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.STRING);

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

    protected SpectrobeEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    /**
     * Identificador de especie por defecto para esta entidad.
     * Ej: "komainu"
     */
    protected abstract String getDefaultSpeciesKey();

    /**
     * Stage inicial por defecto (si aún no lo cargas del JSON).
     * Luego puedes setearlo al spawnear según SpectrobeSpecies.initialStage().
     */
    protected SpectrobeStage getDefaultStage() {
        return SpectrobeStage.CHILD;
    }

    /**
     * Rango válido de variantes de textura.
     * Si siempre son 3, esto se queda en 3. Si algún día cambias, aquí.
     */
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

    private static String safeKey(String key) {
        if (key == null) return "unknown";
        String k = key.trim().toLowerCase();
        return k.isEmpty() ? "unknown" : k;
    }

    /* =========================
       Goals (IA) por stage
       ========================= */

    @Override
    protected void registerGoals() {
        // Goals base comunes
        this.goalSelector.addGoal(1, new FloatGoal(this));

        // Goals según stage
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

    /**
     * IA base para CHILD (tu IA de Komainu actual).
     * Todos los spectrobes en forma CHILD la heredan.
     */
    protected void configureChildGoals() {
        this.goalSelector.addGoal(2, new FollowOwnerSideGoal(this, 1.2));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    /**
     * Por ahora ADULT hereda lo mismo que CHILD.
     * Más adelante puedes añadir combate, protección, etc.
     */
    protected void configureAdultGoals() {
        configureChildGoals();
    }

    /**
     * Por ahora EVOLVED hereda lo mismo que ADULT.
     * Más adelante puedes añadir skills/habilidades especiales.
     */
    protected void configureEvolvedGoals() {
        configureAdultGoals();
    }

    /* =========================
       Animaciones
       ========================= */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0, state -> {
            if (state.isMoving()) state.setAnimation(WALK);
            else state.setAnimation(IDLE);
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

        if (ownerUUID != null) tag.putUUID("Owner", ownerUUID);

        tag.putString("SpeciesKey", getSpeciesKey());
        tag.putInt("TextureVariant", getTextureVariant());
        tag.putString("Stage", getStage().name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.hasUUID("Owner")) ownerUUID = tag.getUUID("Owner");

        if (tag.contains("SpeciesKey")) setSpeciesKey(tag.getString("SpeciesKey"));
        if (tag.contains("TextureVariant")) setTextureVariant(tag.getInt("TextureVariant"));

        if (tag.contains("Stage")) {
            try {
                setStage(SpectrobeStage.valueOf(tag.getString("Stage")));
            } catch (Exception ignored) {
                setStage(SpectrobeStage.CHILD);
            }
        }
    }

    /* =========================
       Auto-asignar owner + defaults al spawnear
       ========================= */

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType reason,
                                        SpawnGroupData spawnData) {

        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);

        if (!this.level().isClientSide) {
            // Defaults si vienen vacíos (por seguridad)
            if ("unknown".equals(getSpeciesKey())) setSpeciesKey(getDefaultSpeciesKey());

            // Variante aleatoria (0..count-1) para eggs/command
            if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND) {
                int count = Math.max(1, getTextureVariantCount());
                setTextureVariant(this.random.nextInt(count));
            }

            // Owner cercano al spawnear por egg/command
            if (ownerUUID == null && (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND)) {
                Player nearest = this.level().getNearestPlayer(this, 6.0);
                if (nearest != null) setOwner(nearest);
            }
        }

        return data;
    }

    /* =========================
       Goal personalizado (follow owner side)
       ========================= */

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