package com.jvprz.spectrobesreforged.common.feature.incubator.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.jetbrains.annotations.Nullable;

/** Music detection and frequency rules, independent of awakening and inventory state. */
public final class IncubatorMusic {
    private IncubatorMusic() {}

    @Nullable
    public static BlockPos findNearestPlayingJukebox(Level level, BlockPos origin, int radius) {
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (BlockPos candidate : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius),
                origin.offset(radius, radius, radius))) {
            double distance = candidate.distSqr(origin);
            if (distance > radius * radius || distance >= nearestDistance
                    || !level.hasChunkAt(candidate)) continue;
            if (level.getBlockEntity(candidate) instanceof JukeboxBlockEntity jukebox
                    && jukebox.getSongPlayer().isPlaying()) {
                nearestDistance = distance;
                nearest = candidate.immutable();
            }
        }
        return nearest;
    }

    public static int frequency(long songTicks, double distance, int radius, int maxFrequency) {
        double mixedWave = (Math.sin(songTicks * 0.42) * 0.45
                + Math.sin(songTicks * 0.19 + 1.7) * 0.35
                + Math.sin(songTicks * 0.075 + 0.4) * 0.20 + 1.0) / 2.0;
        mixedWave = Math.max(0.0, Math.min(1.0, mixedWave));
        double musicalLevel = 0.75 + mixedWave * 0.25;
        double distanceFactor = 1.0 - Math.min(1.0, distance / radius) * 0.70;
        int frequency = (int) Math.round(maxFrequency * musicalLevel * distanceFactor);
        return Math.max(1, Math.min(maxFrequency, frequency));
    }
}
