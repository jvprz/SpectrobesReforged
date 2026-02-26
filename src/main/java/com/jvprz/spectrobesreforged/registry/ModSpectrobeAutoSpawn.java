package com.jvprz.spectrobesreforged.registry;

import com.jvprz.spectrobesreforged.content.prizmod.PrizmodData;
import com.jvprz.spectrobesreforged.content.prizmod.SpectrobeEntry;
import com.jvprz.spectrobesreforged.content.prizmod.SpectrobeManager;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;

import net.minecraft.server.level.ServerPlayer;

public final class ModSpectrobeAutoSpawn {
    private ModSpectrobeAutoSpawn() {}

    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        scheduleRefresh(player);
    }

    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        scheduleRefresh(player);
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        scheduleRefresh(player);
    }

    private static void scheduleRefresh(ServerPlayer player) {
        // Ejecuta en el hilo del server (y ya con el jugador bien asentado en el mundo/dimensión)
        player.server.execute(() -> refreshBabyNow(player));
    }

    private static void refreshBabyNow(ServerPlayer player) {
        PrizmodData data = player.getData(ModAttachments.PRIZMOD.get());
        SpectrobeEntry baby = data.getBabySlot().orElse(null);
        if (baby == null) return;

        // Limpia el bebé en cualquier dimensión y spawnea en la actual
        SpectrobeManager.despawnBabyEverywhere(player.server, player);
        SpectrobeManager.spawnBaby(player.serverLevel(), player, baby);
    }
}