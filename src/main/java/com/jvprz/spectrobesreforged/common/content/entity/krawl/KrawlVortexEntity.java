package com.jvprz.spectrobesreforged.common.content.entity.krawl;

import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlRegistry;
import com.jvprz.spectrobesreforged.common.feature.krawl.factory.KrawlFactory;
import com.jvprz.spectrobesreforged.common.feature.progression.ModProgression;
import com.jvprz.spectrobesreforged.common.feature.vortex.VortexEncounters;
import com.jvprz.spectrobesreforged.common.feature.vortex.VortexProgress;
import com.jvprz.spectrobesreforged.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import com.jvprz.spectrobesreforged.common.registry.ModSounds;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.RenderShape;
import org.joml.Vector3f;
import com.jvprz.spectrobesreforged.common.registry.ModParticles;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import com.jvprz.spectrobesreforged.common.feature.prizmod.logic.SpectrobeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.ArrayList;

public final class KrawlVortexEntity extends Entity implements GeoEntity {
    private static final TagKey<Biome> HOT_BIOMES = TagKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, "hot"));
    private static final TagKey<Biome> TEMPERATE_BIOMES = TagKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, "temperate"));
    private static final int VICTORY_EXPERIENCE = 15;
    public static final String ENCOUNTER_TAG = "SpectrobesVortex";
    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(
            KrawlVortexEntity.class, EntityDataSerializers.BOOLEAN);
    private static final DustParticleOptions SHADOW_MOTE =
            new DustParticleOptions(new Vector3f(0.12F, 0.08F, 0.19F), 0.65F);
    private static final DustParticleOptions VIOLET_MOTE =
            new DustParticleOptions(new Vector3f(0.42F, 0.16F, 0.62F), 0.5F);
    private static final EntityDataAccessor<Boolean> CHASING = SynchedEntityData.defineId(
            KrawlVortexEntity.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation CHASE_ANIMATION = RawAnimation.begin().thenLoop("chase");
    private static final RawAnimation CATCH_ANIMATION = RawAnimation.begin().thenPlayAndHold("catch");
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animationCache; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "vortex", 0, state ->
                state.setAndContinue(entityData.get(ACTIVE) ? CATCH_ANIMATION
                        : entityData.get(CHASING) ? CHASE_ANIMATION : IDLE_ANIMATION)));
    }

    private static final DustParticleOptions KRAWL_BLACK_DUST =
            new DustParticleOptions(new Vector3f(0.02F, 0.0F, 0.03F), 1.8F);
    private static final DustParticleOptions KRAWL_PURPLE_DUST =
            new DustParticleOptions(new Vector3f(0.45F, 0.05F, 0.85F), 1.5F);
    private int deathMoteCooldown = 20;
    private int idleTicks;
    private int dissolveTicks;
    private int contactCooldown;
    private ServerPlayer chaseTarget;
    private int interpolationSteps;
    private Vec3 interpolationTarget = Vec3.ZERO;

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps) {
        if (!level().isClientSide) {
            super.lerpTo(x, y, z, yaw, pitch, steps);
            return;
        }
        interpolationTarget = new Vec3(x, y, z);
        interpolationSteps = Math.max(1, steps);
    }

    public KrawlVortexEntity(EntityType<? extends KrawlVortexEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { builder.define(ACTIVE, false); builder.define(CHASING, false); }
    @Override public boolean isPickable() { return false; }
    public float dissolveProgress(float partialTick) {
        return entityData.get(ACTIVE) ? Math.min(1.0F, (dissolveTicks + partialTick) / 16.0F) : 0;
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { idleTicks = tag.getInt("IdleTicks"); entityData.set(ACTIVE, tag.getBoolean("EncounterStarted")); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("IdleTicks", idleTicks); tag.putBoolean("EncounterStarted", entityData.get(ACTIVE)); }

    private void start(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        var ledger = VortexEncounters.get(level);
        if (ledger.find(getUUID()) != null) {
            player.displayClientMessage(Component.translatable("message.spectrobesreforged.vortex.busy"), true);
            return;
        }
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            player.displayClientMessage(Component.translatable("message.spectrobesreforged.vortex.peaceful"), true);
            return;
        }
        // Select once for the whole wave at the encounter site, including manually summoned vortices.
        var biome = level.getBiome(player.blockPosition());
        String krawlKey = biome.is(HOT_BIOMES) ? "blovant"
                : biome.is(TEMPERATE_BIOMES) ? "viblova" : "blova";
        var definition = KrawlRegistry.getByKey(krawlKey);
        if (definition == null) {
            player.displayClientMessage(Component.translatable("message.spectrobesreforged.vortex.unavailable"), true);
            return;
        }
        var wave = new ArrayList<KrawlEntity>();
        for (int attempt = 0; attempt < 24 && wave.size() < 3; attempt++) {
            double angle = attempt * Math.PI * 2 / 8;
            double radius = 3.5 + (attempt / 8) * 0.8;
            BlockPos column = BlockPos.containing(player.getX() + Math.cos(angle) * radius,
                    player.getY(), player.getZ() + Math.sin(angle) * radius);
            if (!level.hasChunkAt(column)) continue;
            for (int dy = 2; dy >= -3; dy--) {
                BlockPos feet = column.offset(0, dy, 0);
                var mob = KrawlFactory.create(level, definition);
                mob.moveTo(feet.getX() + 0.5, feet.getY(), feet.getZ() + 0.5, 0, 0);
                if (!level.getFluidState(feet).isEmpty()
                        || !level.getBlockState(feet.below()).isSolidRender(level, feet.below())
                        || !level.noCollision(mob)
                        || wave.stream().anyMatch(other -> other.getBoundingBox().inflate(0.3).intersects(mob.getBoundingBox()))) continue;
                mob.setPersistenceRequired();
                mob.setTarget(player);
                mob.getPersistentData().putUUID(ENCOUNTER_TAG, getUUID());
                wave.add(mob);
                break;
            }
        }
        if (wave.size() != 3) { blocked(player); return; }
        var progress = new VortexProgress(player.getUUID(), wave.stream().map(Entity::getUUID).toList());
        if (!ledger.begin(getUUID(), progress)) return;
        for (var mob : wave) {
            if (!level.addFreshEntity(mob)) {
                progress.cancel();
                ledger.setDirty();
                cleanup(level, progress);
                blocked(player);
                discard();
                return;
            }
        }
        SpectrobeManager.despawnBabyEverywhere(player.server, player);
        moveTo(player.getX(), player.getY(), player.getZ(), 0, 0);
        setDeltaMovement(Vec3.ZERO);
        chaseTarget = null;
        entityData.set(ACTIVE, true);
        level.playSound(null, player.blockPosition(), ModSounds.BATTLE_START.get(), SoundSource.HOSTILE, 0.9F, 1.0F);
        player.displayClientMessage(Component.translatable("message.spectrobesreforged.vortex.started"), false);
    }

    private void blocked(Player player) {
        contactCooldown = 60;
        player.displayClientMessage(Component.translatable("message.spectrobesreforged.vortex.blocked"), true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (interpolationSteps > 0) {
                Vec3 step = interpolationTarget.subtract(position()).scale(1.0 / interpolationSteps);
                setPos(position().add(step));
                interpolationSteps--;
            }
            if (entityData.get(ACTIVE)) {
                dissolveTicks = Math.min(16, dissolveTicks + 1);
            } else if (tickCount % 100 == 0) {
                level().playLocalSound(getX(), getY(), getZ(), SoundEvents.PORTAL_AMBIENT,
                        SoundSource.HOSTILE, 0.12F, 0.65F, false);
            }
            if (!entityData.get(ACTIVE) || dissolveTicks < 16) {
                spawnSurfaceMotes();
            }
            if (!entityData.get(ACTIVE)) spawnGroundDust();
            return;
        }
        if (!(level() instanceof ServerLevel level)) return;
        var ledger = VortexEncounters.get(level);
        var progress = ledger.find(getUUID());
        if (progress == null) {
            idleTicks++;
            if (idleTicks >= 24000) { discard(); return; }
            if (contactCooldown > 0) contactCooldown--;
            double beforeY = getY();
            pursue(level);
            // Do not undo a deliberate ledge climb with a downward settle on the same tick.
            if (!entityData.get(ACTIVE) && getY() <= beforeY + 0.0001) settleToGround(level);
            return;
        }
        entityData.set(ACTIVE, true);
        if (progress.status() != VortexProgress.Status.ACTIVE) { discard(); return; }
        if (tickCount % 20 != 0) return;
        var owner = level.getPlayerByUUID(progress.owner());
        boolean nearby = owner instanceof ServerPlayer && owner.isAlive() && !owner.isSpectator()
                && owner.distanceToSqr(this) <= 32 * 32;
        if (level.getDifficulty() == Difficulty.PEACEFUL) progress.cancel();
        progress.advance(20, nearby);
        ledger.setDirty();
        if (progress.status() == VortexProgress.Status.COMPLETED && owner instanceof ServerPlayer player) {
            // Terminal state is committed before issuing rewards; loading it again never reissues them.
            for (int i = 0; i < 2; i++) {
                var item = switch (random.nextInt(3)) {
                    case 0 -> ModItems.POWER_C.get();
                    case 1 -> ModItems.DEFENSE_C.get();
                    default -> ModItems.HEALTH_C.get();
                };
                var reward = new ItemStack(item);
                Vec3 forward = new Vec3(player.getLookAngle().x, 0, player.getLookAngle().z);
                if (forward.lengthSqr() < 0.001) forward = new Vec3(0, 0, 1);
                Vec3 drop = player.position().add(forward.normalize().scale(1.5)).add(0, 0.35, 0);
                if (!level.getBlockState(BlockPos.containing(drop)).getCollisionShape(level,
                        BlockPos.containing(drop)).isEmpty()) drop = player.position().add(0, 0.5, 0);
                var loot = new ItemEntity(level, drop.x, drop.y, drop.z, reward);
                loot.setDeltaMovement(0, 0.12, 0);
                loot.setDefaultPickUpDelay();
                level.addFreshEntity(loot);
            }
            ExperienceOrb.award(level, player.position().add(0, 0.5, 0), VICTORY_EXPERIENCE);
            ModProgression.award(player, "seal_vortex");
            player.displayClientMessage(Component.translatable("message.spectrobesreforged.vortex.completed"), false);
            level.playSound(null, player.blockPosition(), ModSounds.BATTLE_END.get(), SoundSource.PLAYERS, 0.9F, 1.0F);
        } else if (progress.status() == VortexProgress.Status.CANCELLED && owner != null) {
            owner.displayClientMessage(Component.translatable("message.spectrobesreforged.vortex.cancelled"), false);
        } else if (nearby) {
            owner.displayClientMessage(Component.translatable("message.spectrobesreforged.vortex.remaining", progress.remaining()), true);
        }
        if (progress.status() != VortexProgress.Status.ACTIVE) {
            cleanup(level, progress);
            discard();
        }
    }

    private void spawnSurfaceMotes() {
        // Emission is tick-based and client-only; particle settings still control density.
        var viewer = level().getNearestPlayer(this, 48);
        if (viewer == null) return;
        double scale = 1 - dissolveProgress(0);
        if (!entityData.get(ACTIVE) && --deathMoteCooldown <= 0) {
            spawnKrawlGasBurst(scale);
            deathMoteCooldown = 20 + random.nextInt(31);
        }
        int count = viewer.distanceToSqr(this) < 24 * 24 ? 24 : 6;
        for (int i = 0; i < count; i++) {
            double height = (i + random.nextDouble()) / count;
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = (0.06 + Math.pow(height, 1.4) * 1.28) * scale;
            // The custom provider interprets these three parameters as owner id, height and angle.
            level().addParticle(ModParticles.VORTEX_GAS.get(),
                    getX() + Math.cos(angle) * radius,
                    getY() + height * 2.62 * scale + (1 - scale) * 0.6,
                    getZ() + Math.sin(angle) * radius,
                    getId(), height, angle);
        }
        int shadowCount = count == 24 ? 36 : 8;
        for (int i = 0; i < shadowCount; i++) {
            double height = (i + random.nextDouble()) / shadowCount;
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = (0.06 + Math.pow(height, 1.4) * 1.28) * 0.86 * scale;
            level().addParticle(ModParticles.VORTEX_SHADOW_GAS.get(),
                    getX() + Math.cos(angle) * radius,
                    getY() + height * 2.62 * scale + (1 - scale) * 0.6,
                    getZ() + Math.sin(angle) * radius, getId(), height, angle);
        }
        if (tickCount % 2 == 0) {
            double height = random.nextDouble();
            double angle = tickCount * 0.25 + height * Math.PI * 6;
            double radius = (0.08 + height * 1.2) * scale;
            level().addParticle(tickCount % 4 == 0 ? SHADOW_MOTE : VIOLET_MOTE,
                    getX() + Math.cos(angle) * radius, getY() + height * 2.62 * scale,
                    getZ() + Math.sin(angle) * radius,
                    -Math.sin(angle) * 0.04, 0.03, Math.cos(angle) * 0.04);
        }
    }

    private void spawnGroundDust() {
        double dx = getX() - xo;
        double dz = getZ() - zo;
        if (dx * dx + dz * dz < 0.00001 || tickCount % 2 != 0) return;
        BlockPos ground = BlockPos.containing(getX(), getY() - 0.15, getZ());
        var state = level().getBlockState(ground);
        if (state.getRenderShape() == RenderShape.INVISIBLE
                || state.getCollisionShape(level(), ground).isEmpty()
                || state.getCollisionShape(level(), ground).max(Direction.Axis.Y) + ground.getY() < getY() - 0.2) return;
        for (int i = 0; i < 3; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                    getX() + Math.cos(angle) * 0.22, getY() + 0.06,
                    getZ() + Math.sin(angle) * 0.22,
                    -dx * 2 + Math.cos(angle) * 0.05, 0.06 + random.nextDouble() * 0.04,
                    -dz * 2 + Math.sin(angle) * 0.05);
        }
    }

    private void spawnKrawlGasBurst(double scale) {
        // Small intermittent versions of the black/purple dust, portal and smoke death effect.
        for (int i = 0; i < 15; i++) {
            double height = 0.2 + random.nextDouble() * 0.8;
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = (0.1 + height * 1.2) * scale;
            double x = getX() + Math.cos(angle) * radius;
            double y = getY() + height * 2.62 * scale;
            double z = getZ() + Math.sin(angle) * radius;
            double dx = -Math.sin(angle) * 0.04;
            double dz = Math.cos(angle) * 0.04;
            if (i < 8) {
                level().addParticle(i % 2 == 0 ? KRAWL_BLACK_DUST : KRAWL_PURPLE_DUST,
                        x, y, z, dx, 0.04 + random.nextDouble() * 0.05, dz);
            } else {
                level().addParticle(i < 13 ? ParticleTypes.PORTAL : ParticleTypes.LARGE_SMOKE,
                        x, y, z, dx, 0.06, dz);
            }
        }
    }

    private boolean settleToGround(ServerLevel level) {
        // Gravity is disabled for the floating visual, so settle independently of pursuit.
        if (getY() < level.getMinBuildHeight() - 4) {
            discard();
            return true;
        }
        AABB below = getBoundingBox().move(0, -0.03, 0);
        BlockPos min = BlockPos.containing(below.minX, below.minY, below.minZ);
        BlockPos max = BlockPos.containing(below.maxX, below.minY, below.maxZ);
        if (!level.hasChunkAt(min) || !level.hasChunkAt(max)) return true;
        if (!level.noCollision(this, below)) return false;
        // Entity movement clips the descent against actual collision shapes, including slabs.
        move(MoverType.SELF, new Vec3(0, -0.12, 0));
        return true;
    }

    private void pursue(ServerLevel level) {
        entityData.set(CHASING, false);
        if (level.getDifficulty() == Difficulty.PEACEFUL) return;
        if (tickCount % 10 == 0 || chaseTarget == null || !chaseTarget.isAlive()
                || chaseTarget.level() != level || chaseTarget.isSpectator()) {
            chaseTarget = level.players().stream()
                    .filter(p -> p.isAlive() && !p.isSpectator() && p.distanceToSqr(this) <= 24 * 24)
                    .min(java.util.Comparator.comparingDouble(p -> p.distanceToSqr(this))).orElse(null);
        }
        if (chaseTarget == null || chaseTarget.distanceToSqr(this) > 28 * 28) return;
        entityData.set(CHASING, true);
        if (contactCooldown == 0 && getBoundingBox().inflate(0.15).intersects(chaseTarget.getBoundingBox())) {
            start(chaseTarget);
            return;
        }
        Vec3 direction = new Vec3(chaseTarget.getX() - getX(), 0, chaseTarget.getZ() - getZ());
        if (direction.lengthSqr() < 0.001) return;
        Vec3 step = direction.normalize().scale(Math.min(0.045, direction.length()));
        if (!tryMove(level, step)) {
            if (!tryMove(level, new Vec3(step.x, 0, 0))) tryMove(level, new Vec3(0, 0, step.z));
        }
    }

    private boolean tryMove(ServerLevel level, Vec3 horizontal) {
        if (horizontal.horizontalDistanceSqr() < 0.00001) return false;
        double nextX = getX() + horizontal.x;
        double nextZ = getZ() + horizontal.z;
        double halfWidth = getBbWidth() / 2.0;
        AABB footprint = new AABB(nextX - halfWidth, getY() - 2.5, nextZ - halfWidth,
                nextX + halfWidth, getY() + 1.05, nextZ + halfWidth);
        BlockPos min = BlockPos.containing(footprint.minX, getY(), footprint.minZ);
        BlockPos max = BlockPos.containing(footprint.maxX, getY(), footprint.maxZ);
        if (!level.hasChunkAt(min) || !level.hasChunkAt(max)
                || !level.getWorldBorder().isWithinBounds(min)
                || !level.getWorldBorder().isWithinBounds(max)) return false;

        // Collision shapes provide real surface heights, including slabs and stairs.
        double surface = Double.NEGATIVE_INFINITY;
        for (var shape : level.getBlockCollisions(this, footprint)) {
            double top = shape.max(Direction.Axis.Y);
            if (top <= getY() + 1.05 && top >= getY() - 2.5) surface = Math.max(surface, top);
        }
        if (!Double.isFinite(surface)) return false;
        double targetY = surface + 0.025;
        AABB destination = getBoundingBox().move(horizontal.x, targetY - getY(), horizontal.z);
        if (!level.getFluidState(BlockPos.containing(nextX, targetY, nextZ)).isEmpty()
                || !level.noCollision(this, destination)) return false;

        // Lift first when approaching a ledge; otherwise a partial diagonal step intersects its face.
        double rise = Math.max(-0.08, Math.min(0.08, targetY - getY()));
        boolean movedVertically = false;
        if (Math.abs(rise) > 0.0001 && level.noCollision(this, getBoundingBox().move(0, rise, 0))) {
            move(MoverType.SELF, new Vec3(0, rise, 0));
            movedVertically = true;
        }
        if (level.noCollision(this, getBoundingBox().move(horizontal))) {
            move(MoverType.SELF, horizontal);
            return true;
        }
        return movedVertically;
    }

    private static void cleanup(ServerLevel level, VortexProgress progress) {
        for (var id : progress.members()) {
            var mob = level.getEntity(id);
            if (mob != null) mob.discard();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide && level() instanceof ServerLevel level
                && (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED)) {
            var ledger = VortexEncounters.get(level);
            var progress = ledger.find(getUUID());
            if (progress != null && progress.status() == VortexProgress.Status.ACTIVE) {
                progress.cancel();
                ledger.setDirty();
                cleanup(level, progress);
            }
        }
        super.remove(reason);
    }
}
