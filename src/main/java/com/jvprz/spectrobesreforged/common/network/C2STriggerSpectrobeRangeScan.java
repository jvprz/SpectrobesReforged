// src/main/java/com/jvprz/spectrobesreforged/common/network/C2STriggerSpectrobeRangeScan.java
package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.prizmod.logic.SpectrobeManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record C2STriggerSpectrobeRangeScan() implements CustomPacketPayload {

    public static final Type<C2STriggerSpectrobeRangeScan> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    SpectrobesReforged.MODID,
                    "trigger_spectrobe_range_scan"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2STriggerSpectrobeRangeScan> STREAM_CODEC =
            StreamCodec.unit(new C2STriggerSpectrobeRangeScan());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2STriggerSpectrobeRangeScan msg, ServerPlayer player) {
        if (player == null) return;

        ServerLevel level = player.serverLevel();

        SpectrobeEntity spectrobe = SpectrobeManager.findActiveBaby(level, player);
        if (spectrobe == null) return;

        spectrobe.triggerRangePause();
    }
}