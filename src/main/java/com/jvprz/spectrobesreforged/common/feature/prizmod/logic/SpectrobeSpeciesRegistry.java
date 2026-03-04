package com.jvprz.spectrobesreforged.common.feature.prizmod.logic;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class SpectrobeSpeciesRegistry {
    private SpectrobeSpeciesRegistry() {}

    @FunctionalInterface
    public interface BabySpawner {
        boolean spawn(ServerLevel level, ServerPlayer owner, SpectrobeEntry entry);
    }

    private static final Map<String, BabySpawner> BABY_SPAWNERS = new HashMap<>();

    public static void registerBaby(String species, BabySpawner spawner) {
        BABY_SPAWNERS.put(species.toLowerCase(Locale.ROOT), spawner);
    }

    public static BabySpawner getBabySpawner(String species) {
        return BABY_SPAWNERS.get(species.toLowerCase(Locale.ROOT));
    }

    public static boolean canSpawnBaby(String species) {
        return getBabySpawner(species) != null;
    }
}