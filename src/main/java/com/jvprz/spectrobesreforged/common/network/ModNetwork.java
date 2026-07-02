package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
    private ModNetwork() {}

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar =
                event.registrar(SpectrobesReforged.MODID).versioned("1");

        // S2C snapshot (server -> client)
        registrar.playToClient(
                S2CPrizmodSnapshot.TYPE,
                S2CPrizmodSnapshot.STREAM_CODEC,
                (payload, ctx) -> S2CPrizmodSnapshot.handle(payload)
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