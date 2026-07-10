package com.jvprz.spectrobesreforged.common.feature.prizmod;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.network.S2CPrizmodSnapshot;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public final class PrizmodSync {

    private PrizmodSync() {
    }

    public static void sync(ServerPlayer player) {
        PrizmodData data =
                player.getData(ModAttachments.PRIZMOD.get());

        List<S2CPrizmodSnapshot.Entry> box =
                data.getBox()
                        .stream()
                        .map(PrizmodSync::toSnapshotEntry)
                        .toList();

        List<S2CPrizmodSnapshot.Entry> team =
                new ArrayList<>(6);

        for (int slot = 0; slot < 6; slot++) {
            S2CPrizmodSnapshot.Entry entry =
                    data.getTeamSlot(slot)
                            .map(PrizmodSync::toSnapshotEntry)
                            .orElse(null);

            team.add(entry);
        }

        S2CPrizmodSnapshot.Entry baby =
                data.getBabySlot()
                        .map(PrizmodSync::toSnapshotEntry)
                        .orElse(null);

        PacketDistributor.sendToPlayer(
                player,
                new S2CPrizmodSnapshot(
                        box,
                        team,
                        baby
                )
        );
    }

    private static S2CPrizmodSnapshot.Entry toSnapshotEntry(
            SpectrobeEntry entry
    ) {
        return new S2CPrizmodSnapshot.Entry(
                entry.id(),
                entry.species(),
                entry.color(),
                entry.stage(),
                entry.level(),
                entry.hp(),
                entry.hpCur(),
                entry.atk(),
                entry.def(),
                entry.mineralsFed(),
                entry.mineralHpBonus(),
                entry.mineralAtkBonus(),
                entry.mineralDefBonus()
        );
    }
}