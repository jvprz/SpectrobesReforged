package com.jvprz.spectrobesreforged.common.content.entity;

import com.jvprz.spectrobesreforged.common.feature.krawl.*;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public class KrawlEntity extends PathfinderMob implements GeoEntity {

    public static final int ATTACK_ANIMATION_TICKS = 20;
    public static final int ATTACK_DAMAGE_TICK = 10;

    private static final byte DEATH_PARTICLES_EVENT = 60;

    private static final EntityDataAccessor<Integer> ATTACK_TICKS =
            SynchedEntityData.defineId(KrawlEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> VOICE_PITCH =
            SynchedEntityData.defineId(KrawlEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<String> KRAWL_KEY =
            SynchedEntityData.defineId(KrawlEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private String krawlKey;
    private KrawlDefinition definition;

    private int delayedAttackTicks = 0;
    private boolean delayedAttackDamageDone = false;
    private LivingEntity delayedAttackTarget = null;
    private int idleSoundCooldown = 100 + random.nextInt(200);

    public KrawlEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_TICKS, 0);
        builder.define(VOICE_PITCH, 1.0F);
        builder.define(KRAWL_KEY, "");
    }

    public int getAttackTicks() {
        return this.entityData.get(ATTACK_TICKS);
    }

    public float getVoicePitch() {
        return this.entityData.get(VOICE_PITCH);
    }

    private void setVoicePitch(float pitch) {
        this.entityData.set(VOICE_PITCH, pitch);
    }

    private float generateVoicePitch() {
        float pitch = (float) (1.0D + random.nextGaussian() * 0.08D);
        return Math.max(0.80F, Math.min(1.20F, pitch));
    }

    private void setAttackTicks(int ticks) {
        this.entityData.set(ATTACK_TICKS, ticks);
    }

    public boolean isAttackingAnimation() {
        return getAttackTicks() > 0;
    }

    public void startDelayedAttack(LivingEntity target) {
        if (level().isClientSide || target == null || isAttackingAnimation()) {
            return;
        }

        this.delayedAttackTarget = target;
        this.delayedAttackTicks = ATTACK_ANIMATION_TICKS;
        this.delayedAttackDamageDone = false;

        KrawlSoundHelper.playAttack(this);

        setAttackTicks(ATTACK_ANIMATION_TICKS);
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

        String syncedKey = this.entityData.get(KRAWL_KEY);

        if (!syncedKey.isBlank() && !syncedKey.equals(krawlKey)) {
            krawlKey = syncedKey;
            definition = null;
        }

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

    private RawAnimation animation(String name, boolean loop) {
        String id = "animation." + getDefinition().key() + "." + name;

        return loop
                ? RawAnimation.begin().thenLoop(id)
                : RawAnimation.begin().thenPlay(id);
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
        this.entityData.set(KRAWL_KEY, definition.key());

        applyDefinitionStats();
        refreshDimensions();
    }

    private void applyDefinitionStats() {
        KrawlDefinition currentDefinition = getDefinition();

        if (currentDefinition == null) {
            return;
        }

        KrawlStats stats = currentDefinition.stats();

        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(stats.hp());
        Objects.requireNonNull(getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(stats.attack());
        Objects.requireNonNull(getAttribute(Attributes.ARMOR)).setBaseValue(stats.defense());
        Objects.requireNonNull(getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(stats.moveSpeed());
        Objects.requireNonNull(getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(stats.followRange());
        Objects.requireNonNull(getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(stats.knockbackResistance());

        setHealth(getMaxHealth());
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();

        if (!level().isClientSide && getVoicePitch() == 1.0F) {
            setVoicePitch(generateVoicePitch());
        }

        applyDefinitionStats();
        refreshDimensions();
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!level().isClientSide) {
            KrawlSoundHelper.playDeath(this);
            level().broadcastEntityEvent(this, DEATH_PARTICLES_EVENT);
        }

        super.die(damageSource);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);

        if (damaged && !level().isClientSide && isAlive()) {
            KrawlSoundHelper.playHurt(this);
        }

        return damaged;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == DEATH_PARTICLES_EVENT) {
            spawnKrawlDeathParticles();
            return;
        }

        super.handleEntityEvent(id);
    }

    private void spawnKrawlDeathParticles() {
        double centerX = getX();
        double centerY = getY() + getBbHeight() * 0.45D;
        double centerZ = getZ();

        double width = Math.max(0.6D, getBbWidth());
        double height = Math.max(1.0D, getBbHeight());

        DustParticleOptions blackDust = new DustParticleOptions(
                new Vector3f(0.02F, 0.0F, 0.03F),
                1.8F
        );

        DustParticleOptions purpleDust = new DustParticleOptions(
                new Vector3f(0.45F, 0.05F, 0.85F),
                1.5F
        );

        for (int i = 0; i < 80; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double distance = random.nextDouble() * width * 0.45D;

            double x = centerX + Math.cos(angle) * distance;
            double y = getY() + random.nextDouble() * height;
            double z = centerZ + Math.sin(angle) * distance;

            double dx = Math.cos(angle) * (0.025D + random.nextDouble() * 0.045D);
            double dy = 0.035D + random.nextDouble() * 0.08D;
            double dz = Math.sin(angle) * (0.025D + random.nextDouble() * 0.045D);

            level().addParticle(
                    random.nextBoolean() ? blackDust : purpleDust,
                    x, y, z,
                    dx, dy, dz
            );
        }

        for (int i = 0; i < 45; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double distance = random.nextDouble() * width * 0.35D;

            double x = centerX + Math.cos(angle) * distance;
            double y = centerY + (random.nextDouble() - 0.5D) * height * 0.5D;
            double z = centerZ + Math.sin(angle) * distance;

            double dx = Math.cos(angle) * 0.035D;
            double dy = 0.06D + random.nextDouble() * 0.10D;
            double dz = Math.sin(angle) * 0.035D;

            level().addParticle(
                    ParticleTypes.PORTAL,
                    x, y, z,
                    dx, dy, dz
            );
        }

        for (int i = 0; i < 25; i++) {
            double x = centerX + (random.nextDouble() - 0.5D) * width;
            double y = centerY + (random.nextDouble() - 0.5D) * height * 0.4D;
            double z = centerZ + (random.nextDouble() - 0.5D) * width;

            double dx = (random.nextDouble() - 0.5D) * 0.08D;
            double dy = 0.04D + random.nextDouble() * 0.08D;
            double dz = (random.nextDouble() - 0.5D) * 0.08D;

            level().addParticle(
                    ParticleTypes.LARGE_SMOKE,
                    x, y, z,
                    dx, dy, dz
            );
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (krawlKey != null) {
            tag.putString("KrawlKey", krawlKey);
        }

        tag.putFloat("VoicePitch", getVoicePitch());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("KrawlKey")) {
            this.krawlKey = tag.getString("KrawlKey");
            this.definition = KrawlRegistry.getByKey(krawlKey);
        }

        if (tag.contains("VoicePitch")) {
            setVoicePitch(tag.getFloat("VoicePitch"));
        }

        applyDefinitionStats();
        refreshDimensions();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new KrawlMeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            tickIdleSound();

            if (getAttackTicks() > 0) {
                setAttackTicks(getAttackTicks() - 1);
            }

            if (delayedAttackTicks > 0) {
                delayedAttackTicks--;

                if (!delayedAttackDamageDone && delayedAttackTicks == ATTACK_DAMAGE_TICK) {
                    if (
                            delayedAttackTarget != null
                                    && delayedAttackTarget.isAlive()
                                    && this.isWithinMeleeAttackRange(delayedAttackTarget)
                                    && this.getSensing().hasLineOfSight(delayedAttackTarget)
                    ) {
                        super.doHurtTarget(delayedAttackTarget);
                    }

                    delayedAttackDamageDone = true;
                }

                if (delayedAttackTicks <= 0) {
                    delayedAttackTarget = null;
                    delayedAttackDamageDone = false;
                }
            }
        }
    }

    private void tickIdleSound() {
        if (!isAlive() || isAttackingAnimation()) {
            return;
        }

        if (getTarget() != null) {
            return;
        }

        idleSoundCooldown--;

        if (idleSoundCooldown <= 0) {
            KrawlSoundHelper.playIdle(this);
            idleSoundCooldown = 160 + random.nextInt(240);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "main_controller",
                5,
                state -> {
                    if (isAttackingAnimation()) {
                        state.setAnimation(animation("attack", false));
                        return PlayState.CONTINUE;
                    }

                    if (state.isMoving()) {
                        state.setAnimation(animation("walk", true));
                    } else {
                        state.setAnimation(animation("idle", true));
                    }

                    return PlayState.CONTINUE;
                }
        ));
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}