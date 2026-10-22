package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.common.feature.prizmod.PrizmodSync;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import net.minecraft.server.level.ServerPlayer;

/** Compatibility entry point; all snapshot construction lives in PrizmodSync. */
public final class ModSnapshotSender {
    private ModSnapshotSender() {}

    public static void sendSnapshot(ServerPlayer player, PrizmodData data) {
        PrizmodSync.sync(player, data);
    }
}
