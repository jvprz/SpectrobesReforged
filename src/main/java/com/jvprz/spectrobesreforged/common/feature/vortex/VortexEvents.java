package com.jvprz.spectrobesreforged.common.feature.vortex;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.config.Config;
import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlEntity;
import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlVortexEntity;
import com.jvprz.spectrobesreforged.common.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.GameRules;
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
    private static final TagKey<Block> NATURAL_GROUND = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, "vortex_ground"));

    public static void restAfterEncounter(ServerPlayer player) {
        player.getPersistentData().putLong(VortexRules.NEXT_DISCOVERY,
                player.serverLevel().getGameTime() + Config.cooldownTicks());
    }

    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof ServerPlayer player)
            for(var dimension:player.server.getAllLevels())VortexEncounters.get(dimension).playerDied(player,dimension);
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

    @SubscribeEvent public static void pendingRewards(PlayerTickEvent.Post event){
        if(event.getEntity() instanceof ServerPlayer player&&player.tickCount%20==0)VortexEncounters.payPending(player);
    }
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.LOWEST) public static void raidDrops(net.neoforged.neoforge.event.entity.living.LivingDropsEvent event){
        if(event.getEntity() instanceof KrawlEntity mob&&mob.getPersistentData().hasUUID(KrawlVortexEntity.ENCOUNTER_TAG))event.getDrops().clear();
    }
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.LOWEST) public static void raidExperience(net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent event){
        if(event.getEntity() instanceof KrawlEntity mob&&mob.getPersistentData().hasUUID(KrawlVortexEntity.ENCOUNTER_TAG))event.setDroppedExperience(0);
    }

    @SubscribeEvent
    public static void discover(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || player.tickCount % Config.discoveryTicks() != 0
                || player.isCreative() || player.isSpectator() || !player.isAlive()) return;
        ServerLevel level = player.serverLevel();
        if (!Config.naturalVortices() || !level.dimension().equals(Level.OVERWORLD) || level.getDifficulty() == Difficulty.PEACEFUL
                || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                || level.getGameTime() < player.getPersistentData().getLong(VortexRules.NEXT_DISCOVERY)
                || VortexEncounters.companionSuppressed(player)
                || !level.canSeeSky(player.blockPosition())
                || level.random.nextDouble() >= Config.discoveryChance()) return;
        if (!level.getEntitiesOfClass(KrawlVortexEntity.class,
                player.getBoundingBox().inflate(VortexRules.NEARBY_VORTEX_RADIUS)).isEmpty()) return;
        int loadedVortices = 0;
        for (var entity : level.getAllEntities()) {
            if (entity instanceof KrawlVortexEntity && ++loadedVortices >= Config.maxVortices()) return;
        }
        for (int attempt = 0; attempt < VortexRules.SITE_ATTEMPTS; attempt++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = VortexRules.MIN_SPAWN_DISTANCE + level.random.nextInt(
                    VortexRules.MAX_SPAWN_DISTANCE - VortexRules.MIN_SPAWN_DISTANCE + 1);
            BlockPos column = BlockPos.containing(player.getX() + Math.cos(angle) * distance, player.getY(),
                    player.getZ() + Math.sin(angle) * distance);
            if (!level.hasChunkAt(column)) continue;
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column);
            if (!level.getBlockState(pos.below()).is(NATURAL_GROUND)
                    || !level.getFluidState(pos).isEmpty() || !level.canSeeSky(pos)) continue;
            boolean clear = true;
            for (int y = 0; y < 3; y++) if (!level.getBlockState(pos.above(y)).isAir()) clear = false;
            if (!clear) continue;
            var vortex = ModEntities.KRAWL_VORTEX.get().create(level);
            if (vortex == null) return;
            vortex.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            if (level.getWorldBorder().isWithinBounds(pos) && level.noCollision(vortex)
                    && level.players().stream().noneMatch(p -> !p.isSpectator() && p.distanceToSqr(vortex) < VortexRules.PLAYER_EXCLUSION_RADIUS * VortexRules.PLAYER_EXCLUSION_RADIUS)
                    && level.addFreshEntity(vortex)) {
                restAfterEncounter(player);
                return;
            }
        }
    }
}
