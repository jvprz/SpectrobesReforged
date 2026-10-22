package com.jvprz.spectrobesreforged.common.content.entity.krawl;

import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlRegistry;
import com.jvprz.spectrobesreforged.common.feature.krawl.factory.KrawlFactory;
import com.jvprz.spectrobesreforged.common.feature.progression.ModProgression;
import com.jvprz.spectrobesreforged.common.feature.vortex.VortexEncounters;
import com.jvprz.spectrobesreforged.common.feature.vortex.VortexProgress;
import com.jvprz.spectrobesreforged.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import com.jvprz.spectrobesreforged.common.registry.ModSounds;
import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.ArrayList;

public final class KrawlVortexEntity extends Entity {
    public static final String ENCOUNTER_TAG = "SpectrobesVortex";
    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(
            KrawlVortexEntity.class, EntityDataSerializers.BOOLEAN);
    private static final DustParticleOptions SHADOW_MOTE =
            new DustParticleOptions(new Vector3f(0.12F, 0.08F, 0.19F), 0.65F);
    private static final DustParticleOptions VIOLET_MOTE =
            new DustParticleOptions(new Vector3f(0.42F, 0.16F, 0.62F), 0.5F);
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

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { builder.define(ACTIVE, false); }
    @Override public boolean isPickable() { return false; }
    public float dissolveProgress(float partialTick) {
        return entityData.get(ACTIVE) ? Math.min(1.0F, (dissolveTicks + partialTick) / 12.0F) : 0;
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
        var definition = KrawlRegistry.getByKey("blova");
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
                dissolveTicks = Math.min(12, dissolveTicks + 1);
            } else if (tickCount % 100 == 0) {
                level().playLocalSound(getX(), getY(), getZ(), SoundEvents.PORTAL_AMBIENT,
                        SoundSource.HOSTILE, 0.12F, 0.65F, false);
            }
            if (tickCount % 2 == 0 && (!entityData.get(ACTIVE) || dissolveTicks < 12)) {
                spawnSurfaceMotes();
            }
            return;
        }
        if (!(level() instanceof ServerLevel level)) return;
        var ledger = VortexEncounters.get(level);
        var progress = ledger.find(getUUID());
        if (progress == null) {
            idleTicks++;
            if (idleTicks >= 24000) { discard(); return; }
            if (contactCooldown > 0) contactCooldown--;
            pursue(level);
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
        double scale = entityData.get(ACTIVE) ? Math.max(0, 1 - dissolveTicks / 12.0) : 1;
        for (int i = 0; i < 2; i++) {
            double height = 0.1 + random.nextDouble() * 0.85;
            double angle = tickCount * 0.15 + height * Math.PI * 6 + i * Math.PI;
            double radius = (0.12 + Math.pow(height, 0.72) * 0.96) * 1.05 * scale;
            level().addParticle(i == 0 ? SHADOW_MOTE : VIOLET_MOTE,
                    getX() + Math.cos(angle) * radius,
                    getY() + height * 2.65 * scale + (1 - scale) * 0.6,
                    getZ() + Math.sin(angle) * radius,
                    -Math.sin(angle) * 0.012, 0.018, Math.cos(angle) * 0.012);
        }
    }

    private void pursue(ServerLevel level) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return;
        if (tickCount % 10 == 0 || chaseTarget == null || !chaseTarget.isAlive()
                || chaseTarget.level() != level || chaseTarget.isSpectator()) {
            chaseTarget = level.players().stream()
                    .filter(p -> p.isAlive() && !p.isSpectator() && p.distanceToSqr(this) <= 24 * 24)
                    .min(java.util.Comparator.comparingDouble(p -> p.distanceToSqr(this))).orElse(null);
        }
        if (chaseTarget == null || chaseTarget.distanceToSqr(this) > 28 * 28) return;
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
