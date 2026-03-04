package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public final class ModSnapshotSender {
    private ModSnapshotSender() {}

    public static void sendSnapshot(ServerPlayer sp, PrizmodData data) {

        var box = data.getBox().stream()
                .map(e -> new S2CPrizmodSnapshot.Entry(
                        e.id(), e.species(),
                        e.color(),
                        e.stage(),
                        e.level(),
                        e.hp(), e.atk(), e.def()
                ))
                .toList();

        List<S2CPrizmodSnapshot.Entry> team = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            var opt = data.getTeamSlot(i);
            team.add(opt.map(e -> new S2CPrizmodSnapshot.Entry(
                    e.id(), e.species(),
                    e.color(),
                    e.stage(),
                    e.level(),
                    e.hp(), e.atk(), e.def()
            )).orElse(null));
        }

        var baby = data.getBabySlot()
                .map(e -> new S2CPrizmodSnapshot.Entry(
                        e.id(), e.species(),
                        e.color(),
                        e.stage(),
                        e.level(),
                        e.hp(), e.atk(), e.def()
                ))
                .orElse(null);

        PacketDistributor.sendToPlayer(sp, new S2CPrizmodSnapshot(box, team, baby));
    }
}