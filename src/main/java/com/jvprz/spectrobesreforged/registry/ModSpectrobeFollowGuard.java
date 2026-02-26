package com.jvprz.spectrobesreforged.registry;

import com.jvprz.spectrobesreforged.content.prizmod.PrizmodData;
import com.jvprz.spectrobesreforged.content.prizmod.SpectrobeEntry;
import com.jvprz.spectrobesreforged.content.prizmod.SpectrobeManager;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.server.level.ServerPlayer;

public final class ModSpectrobeFollowGuard {
    private ModSpectrobeFollowGuard() {}

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // cada 20 ticks (1 segundo) para no spamear
        if ((player.tickCount % 20) != 0) return;

        PrizmodData data = player.getData(ModAttachments.PRIZMOD.get());
        SpectrobeEntry baby = data.getBabySlot().orElse(null);
        if (baby == null) return;

        // Si no hay bebé en ESTA dimensión cerca, lo recuperamos
        boolean hasBabyHere = SpectrobeManager.hasBabyNearby(player.serverLevel(), player);

        if (!hasBabyHere) {
            SpectrobeManager.despawnBabyEverywhere(player.server, player);
            SpectrobeManager.spawnBaby(player.serverLevel(), player, baby);
        }
    }
}