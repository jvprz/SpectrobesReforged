// src/main/java/com/jvprz/spectrobesreforged/common/feature/prizmod/logic/SpectrobeManager.java
package com.jvprz.spectrobesreforged.common.feature.prizmod.logic;

import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpecies;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public final class SpectrobeManager {
    private SpectrobeManager() {}

    public static void despawnBaby(ServerLevel level, ServerPlayer owner) {
        var list = level.getEntitiesOfClass(SpectrobeEntity.class, owner.getBoundingBox().inflate(256));
        for (SpectrobeEntity e : list) {
            var tag = e.getPersistentData();

            boolean isPrizmodBaby = tag.getBoolean("PrizmodBaby");
            boolean sameOwner = tag.hasUUID("PrizmodOwner") && tag.getUUID("PrizmodOwner").equals(owner.getUUID());

            var o = e.getOwner().orElse(null);
            boolean ownerMatchFallback = (o != null && o.getUUID().equals(owner.getUUID()));

            if ((isPrizmodBaby && sameOwner) || ownerMatchFallback) {
                e.discard();
            }
        }
    }

    public static void despawnBabyEverywhere(MinecraftServer server, ServerPlayer owner) {
        for (ServerLevel level : server.getAllLevels()) {
            despawnBaby(level, owner);
        }
    }

    public static boolean spawnBaby(ServerLevel level, ServerPlayer owner, SpectrobeEntry entry) {
        if (entry == null) return false;

        // KO => no spawnea aunque esté equipado
        if (entry.hpCur() <= 0) return false;

        SpectrobeSpecies species = SpectrobeSpeciesRegistry.getByKey(entry.species());
        if (species == null) return false;

        SpectrobeEntity spectrobe = ModEntities.SPECTROBE.get().create(level);
        if (spectrobe == null) return false;

        Vec3 pos = findSafeSpawnNearPlayer(level, owner);

        spectrobe.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), 0);
        spectrobe.setOwner(owner);

        // especie / stage / chroma desde entry
        spectrobe.setSpeciesKey(entry.species());
        spectrobe.setStage(parseStage(entry.stage()));
        spectrobe.setTextureVariant(entry.color());

        // HP real desde Prizmod
        var maxHpAttr = spectrobe.getAttribute(Attributes.MAX_HEALTH);
        if (maxHpAttr != null) {
            maxHpAttr.setBaseValue(Math.max(1, entry.hp()));
        }

        float hpNow = (float) Math.max(0, Math.min(entry.hpCur(), entry.hp()));
        spectrobe.setHealth(Math.max(1.0f, hpNow));

        // Reaplica IA según el stage cargado
        spectrobe.refreshGoalsForCurrentStage();

        // Mark Prizmod
        spectrobe.getPersistentData().putBoolean("PrizmodBaby", true);
        spectrobe.getPersistentData().putUUID("PrizmodOwner", owner.getUUID());
        spectrobe.getPersistentData().putUUID("SpectrobeId", entry.id());

        level.addFreshEntity(spectrobe);

        if (!level.noCollision(spectrobe)) {
            spectrobe.teleportTo(owner.getX(), owner.getY() + 0.2, owner.getZ());
        }

        return true;
    }

    private static com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeStage parseStage(String raw) {
        if (raw == null || raw.isBlank()) {
            return com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeStage.CHILD;
        }

        try {
            return com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeStage.valueOf(raw.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (Exception ignored) {
            return com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeStage.CHILD;
        }
    }

    private static Vec3 findSafeSpawnNearPlayer(ServerLevel level, ServerPlayer player) {
        BlockPos base = player.blockPosition();

        int[] rs = {1, 2, 3};
        for (int r : rs) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx == 0 && dz == 0) continue;

                    BlockPos probe = base.offset(dx, 0, dz);
                    BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, probe);

                    BlockPos feet = top.above();
                    BlockPos head = feet.above();

                    BlockState belowState = level.getBlockState(feet.below());
                    BlockState feetState = level.getBlockState(feet);
                    BlockState headState = level.getBlockState(head);

                    boolean hasFloor = belowState.isSolid();
                    boolean airFeet = feetState.getCollisionShape(level, feet).isEmpty();
                    boolean airHead = headState.getCollisionShape(level, head).isEmpty();

                    if (hasFloor && airFeet && airHead) {
                        return Vec3.atBottomCenterOf(feet);
                    }
                }
            }
        }

        return new Vec3(player.getX() + 0.8, player.getY() + 0.1, player.getZ() + 0.8);
    }

    public static boolean hasBabyNearby(ServerLevel level, ServerPlayer owner) {
        var list = level.getEntitiesOfClass(SpectrobeEntity.class, owner.getBoundingBox().inflate(64));
        for (SpectrobeEntity e : list) {
            var tag = e.getPersistentData();
            boolean isPrizmodBaby = tag.getBoolean("PrizmodBaby");
            boolean sameOwner = tag.hasUUID("PrizmodOwner") && tag.getUUID("PrizmodOwner").equals(owner.getUUID());
            if (isPrizmodBaby && sameOwner) return true;
        }
        return false;
    }
}