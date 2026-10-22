package com.jvprz.spectrobesreforged.common.feature.vortex;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlEntity;
import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlVortexEntity;
import com.jvprz.spectrobesreforged.common.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SpectrobesReforged.MODID)
public final class VortexEvents {
    private VortexEvents() {}

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof KrawlEntity mob && mob.level() instanceof ServerLevel level
                && mob.getPersistentData().hasUUID(KrawlVortexEntity.ENCOUNTER_TAG)) {
            VortexEncounters.get(level).defeated(mob.getPersistentData().getUUID(KrawlVortexEntity.ENCOUNTER_TAG), mob.getUUID());
        }
    }

    @SubscribeEvent
    public static void guardEncounterMob(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof KrawlEntity mob) || mob.tickCount % 20 != 0
                || !(mob.level() instanceof ServerLevel level)
                || !mob.getPersistentData().hasUUID(KrawlVortexEntity.ENCOUNTER_TAG)) return;
        var encounter = VortexEncounters.get(level).find(mob.getPersistentData().getUUID(KrawlVortexEntity.ENCOUNTER_TAG));
        if (encounter != null && encounter.status() == VortexProgress.Status.ACTIVE && mob.tickCount >= 100
                && level.getEntity(mob.getPersistentData().getUUID(KrawlVortexEntity.ENCOUNTER_TAG)) == null) {
            encounter.cancel();
            VortexEncounters.get(level).setDirty();
        }
        if (encounter == null || encounter.status() != VortexProgress.Status.ACTIVE
                || !encounter.members().contains(mob.getUUID())) mob.discard();
    }

    @SubscribeEvent
    public static void discover(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 1200 != 0
                || player.isCreative() || player.isSpectator() || !player.isAlive()) return;
        ServerLevel level = player.serverLevel();
        if (!level.dimension().equals(Level.OVERWORLD) || level.getDifficulty() == Difficulty.PEACEFUL
                || level.random.nextInt(4) != 0) return;
        if (!level.getEntitiesOfClass(KrawlVortexEntity.class,
                player.getBoundingBox().inflate(128)).isEmpty()) return;
        double angle = level.random.nextDouble() * Math.PI * 2;
        double distance = 40 + level.random.nextInt(25);
        BlockPos column = BlockPos.containing(player.getX() + Math.cos(angle) * distance, player.getY(),
                player.getZ() + Math.sin(angle) * distance);
        if (!level.hasChunkAt(column)) return;
        BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column);
        var floor = level.getBlockState(pos.below());
        if (!(floor.is(BlockTags.DIRT) || floor.is(BlockTags.SAND) || floor.is(BlockTags.BASE_STONE_OVERWORLD))
                || !level.getFluidState(pos).isEmpty() || !level.canSeeSky(pos)) return;
        for (int y = 0; y < 3; y++) if (!level.getBlockState(pos.above(y)).isAir()) return;
        var vortex = ModEntities.KRAWL_VORTEX.get().create(level);
        if (vortex == null) return;
        vortex.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        if (level.getWorldBorder().isWithinBounds(pos) && level.noCollision(vortex)
                && level.players().stream().noneMatch(p -> p.distanceToSqr(vortex) < 32 * 32)) {
            level.addFreshEntity(vortex);
        }
    }
}
