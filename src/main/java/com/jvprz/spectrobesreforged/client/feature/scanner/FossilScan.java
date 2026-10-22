package com.jvprz.spectrobesreforged.client.feature.scanner;

import com.jvprz.spectrobesreforged.common.content.block.FossilOreBlock;
import com.jvprz.spectrobesreforged.common.content.block.MineralOreBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/** Incremental search: limits work even when there are no matching blocks. */
public final class FossilScan {
    private static final int POSITIONS_PER_TICK = 2048;
    private static final int RESCAN_DELAY = 10;
    private Iterator<BlockPos> cursor;
    private BlockPos origin;
    private double scanRadius;
    private long nextScan;
    private final List<BlockPos> pending = new ArrayList<>();
    private List<BlockPos> results = List.of();

    public List<BlockPos> results() { return results; }

    /** Returns true only when a complete result is ready for publication. */
    public boolean tick(ClientLevel level, BlockPos center, double radius, int depth, int limit) {
        return tick(level.getGameTime(), center, radius, depth, limit,
                level.getMinBuildHeight(), level.getMaxBuildHeight(), level::hasChunkAt,
                pos -> {
                    var block = level.getBlockState(pos).getBlock();
                    return block instanceof FossilOreBlock || block instanceof MineralOreBlock;
                });
    }

    boolean tick(long gameTime, BlockPos center, double radius, int depth, int limit,
            int minY, int maxY, Predicate<BlockPos> loaded, Predicate<BlockPos> isTarget) {
        if (cursor != null && (scanRadius != radius || origin.distSqr(center) > radius * radius)) {
            cursor = null;
            nextScan = 0;
        }
        if (cursor == null) {
            if (gameTime < nextScan) return false;
            origin = center.immutable();
            scanRadius = radius;
            int r = Mth.ceil(radius);
            cursor = BlockPos.betweenClosed(origin.offset(-r, -depth, -r),
                    origin.offset(r, 0, r)).iterator();
            pending.clear();
        }
        int remaining = POSITIONS_PER_TICK;
        while (remaining-- > 0 && cursor.hasNext() && pending.size() < limit) {
            BlockPos pos = cursor.next();
            double dx = pos.getX() - origin.getX();
            double dz = pos.getZ() - origin.getZ();
            if (dx * dx + dz * dz > scanRadius * scanRadius
                    || pos.getY() < minY || pos.getY() >= maxY
                    || !loaded.test(pos)) continue;
            if (isTarget.test(pos)) {
                pending.add(pos.immutable());
            }
        }
        if (cursor.hasNext() && pending.size() < limit) return false;
        results = List.copyOf(pending);
        cursor = null;
        nextScan = gameTime + RESCAN_DELAY;
        return true;
    }
}
