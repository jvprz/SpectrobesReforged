// src/main/java/com/jvprz/spectrobesreforged/common/feature/prizmod/logic/SpectrobeManager.java
package com.jvprz.spectrobesreforged.common.feature.prizmod.logic;

import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeStage;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

        String species = entry.species();

        if (species == null) return false;

        switch (species.toLowerCase()) {

            case "komainu":
                return spawnKomainu(level, owner, entry);

            default:
                return false;
        }
    }

    public static boolean spawnKomainu(ServerLevel level, ServerPlayer owner, SpectrobeEntry entry) {
        SpectrobeEntity komainu = ModEntities.KOMAINU.get().create(level);
        if (komainu == null) return false;

        Vec3 pos = findSafeSpawnNearPlayer(level, owner);

        // Posición + owner
        komainu.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), 0);
        komainu.setOwner(owner);

        // ===== APPLY ENTRY DATA (IMPORTANT) =====
        // species key (para que el renderer encuentre la species en el registry)
        komainu.setSpeciesKey(entry.species());

        // stage (si en tu entry lo guardas como String)
        // fallback seguro si viniera vacío o inválido
        try {
            komainu.setStage(SpectrobeStage.valueOf(entry.stage().toUpperCase()));
        } catch (Exception ignored) {
            komainu.setStage(SpectrobeStage.CHILD);
        }

        // texture variant (tu "color" 0/1/2)
        komainu.setTextureVariant(entry.color());
        // =======================================

        // Marca Prizmod
        komainu.getPersistentData().putBoolean("PrizmodBaby", true);
        komainu.getPersistentData().putUUID("PrizmodOwner", owner.getUUID());
        komainu.getPersistentData().putUUID("SpectrobeId", entry.id());

        level.addFreshEntity(komainu);

        if (!level.noCollision(komainu)) {
            komainu.teleportTo(owner.getX(), owner.getY() + 0.2, owner.getZ());
        }

        return true;
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