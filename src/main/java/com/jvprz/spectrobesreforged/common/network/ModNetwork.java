package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
    private ModNetwork() {}

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar =
                event.registrar(SpectrobesReforged.MODID).versioned("6");

        registrar.playToClient(S2CExcavationCoop.TYPE,S2CExcavationCoop.STREAM_CODEC,
                (packet,ctx)->ctx.enqueueWork(()->com.jvprz.spectrobesreforged.client.event.ClientPayloadHandlers.excavation(packet)));
        registrar.playToServer(C2SExcavationCursor.TYPE,C2SExcavationCursor.STREAM_CODEC,(packet,ctx)->{
            if(ctx.player() instanceof net.minecraft.server.level.ServerPlayer player)C2SExcavationCursor.handle(packet,player);
        });
        registrar.playToClient(S2CGameData.TYPE,S2CGameData.STREAM_CODEC,
                (packet,ctx)->ctx.enqueueWork(()->com.jvprz.spectrobesreforged.client.event.ClientPayloadHandlers.definitions(packet)));
        registrar.playToServer(C2SOpenPrizmod.TYPE,C2SOpenPrizmod.STREAM_CODEC,(packet,ctx)->{
            if(ctx.player() instanceof net.minecraft.server.level.ServerPlayer player)C2SOpenPrizmod.handle(packet,player);
        });

        registrar.playToServer(C2SExcavate.TYPE, C2SExcavate.STREAM_CODEC, (payload, ctx) -> {
            if (ctx.player() instanceof net.minecraft.server.level.ServerPlayer player) C2SExcavate.handle(payload, player);
        });

        registrar.playToServer(C2SFeedSpectrobe.TYPE,C2SFeedSpectrobe.STREAM_CODEC,(payload,ctx)->{
            if(ctx.player() instanceof net.minecraft.server.level.ServerPlayer player)C2SFeedSpectrobe.handle(payload,player);
        });

        // S2C snapshot (server -> client)
        registrar.playToClient(
                S2CPrizmodSnapshot.TYPE,
                S2CPrizmodSnapshot.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> com.jvprz.spectrobesreforged.client.event.ClientPayloadHandlers.prizmod(payload))
        );

        // C2S move spectrobe
        registrar.playToServer(
                C2SMoveSpectrobe.TYPE,
                C2SMoveSpectrobe.STREAM_CODEC,
                (payload, ctx) -> {
                    var p = ctx.player();
                    if (p instanceof net.minecraft.server.level.ServerPlayer sp) {
                        C2SMoveSpectrobe.handle(payload, sp);
                    }
                }
        );

        // C2S trigger temporary range scan
        registrar.playToServer(
                C2STriggerSpectrobeRangeScan.TYPE,
                C2STriggerSpectrobeRangeScan.STREAM_CODEC,
                (payload, ctx) -> {
                    var p = ctx.player();
                    if (p instanceof net.minecraft.server.level.ServerPlayer sp) {
                        C2STriggerSpectrobeRangeScan.handle(payload, sp);
                    }
                }
        );
    }
}