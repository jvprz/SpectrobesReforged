package com.jvprz.spectrobesreforged.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.content.prizmod.PrizmodData;
import com.jvprz.spectrobesreforged.content.prizmod.SpectrobeEntry;
import com.jvprz.spectrobesreforged.registry.ModAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public record C2SMoveSpectrobe(int fromType, int fromIndex, int toType, int toIndex)
        implements CustomPacketPayload {

    // 0=BOX, 1=TEAM, 2=BABY

    public static final Type<C2SMoveSpectrobe> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, "move_spectrobe"));

    public static final StreamCodec<FriendlyByteBuf, C2SMoveSpectrobe> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> {
                        buf.writeVarInt(msg.fromType);
                        buf.writeVarInt(msg.fromIndex);
                        buf.writeVarInt(msg.toType);
                        buf.writeVarInt(msg.toIndex);
                    },
                    buf -> new C2SMoveSpectrobe(
                            buf.readVarInt(),
                            buf.readVarInt(),
                            buf.readVarInt(),
                            buf.readVarInt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SMoveSpectrobe msg, ServerPlayer player) {
        PrizmodData data = player.getData(ModAttachments.PRIZMOD.get());

        var beforeBaby = data.getBabySlot().orElse(null);

        if (msg.fromType < 0 || msg.fromType > 2) return;
        if (msg.toType < 0 || msg.toType > 2) return;

        if (msg.fromType == 1 && (msg.fromIndex < 0 || msg.fromIndex > 5)) return;
        if (msg.toType == 1 && (msg.toIndex < 0 || msg.toIndex > 5)) return;
        if (msg.fromType == 2 && msg.fromIndex != 0) return;
        if (msg.toType == 2 && msg.toIndex != 0) return;

        SpectrobeEntry moving = switch (msg.fromType) {
            case 0 -> data.getBoxAt(msg.fromIndex);
            case 1 -> data.getTeamSlot(msg.fromIndex).orElse(null);
            case 2 -> data.getBabySlot().orElse(null);
            default -> null;
        };

        if (moving == null) return;

        // Reglas de slots
        if (msg.toType == 2 && !moving.baby()) return; // baby slot solo bebés
        if (msg.toType == 1 && moving.baby()) return;  // team slot solo adultos

        // Remover del origen
        switch (msg.fromType) {
            case 0 -> data.removeBoxAt(msg.fromIndex);
            case 1 -> data.setTeamSlot(msg.fromIndex, null);
            case 2 -> data.setBabySlot(null);
        }

        // Obtener destino (para swap)
        SpectrobeEntry displaced = switch (msg.toType) {
            case 0 -> data.getBoxAt(msg.toIndex);
            case 1 -> data.getTeamSlot(msg.toIndex).orElse(null);
            case 2 -> data.getBabySlot().orElse(null);
            default -> null;
        };

        // Si destino ocupado, lo quitamos del destino
        if (displaced != null) {
            switch (msg.toType) {
                case 0 -> data.removeFromBox(displaced.id());
                case 1 -> data.setTeamSlot(msg.toIndex, null);
                case 2 -> data.setBabySlot(null);
            }

            // Y lo devolvemos al origen (swap)
            switch (msg.fromType) {
                case 0 -> data.insertBoxAt(msg.fromIndex, displaced);
                case 1 -> data.setTeamSlot(msg.fromIndex, displaced);
                case 2 -> data.setBabySlot(displaced);
            }
        }

        // Poner moving en destino
        switch (msg.toType) {
            case 0 -> data.insertBoxAt(Math.min(msg.toIndex, data.getBox().size()), moving);
            case 1 -> data.setTeamSlot(msg.toIndex, moving);
            case 2 -> data.setBabySlot(moving);
        }

        var afterBaby = data.getBabySlot().orElse(null);

        boolean changed =
                (beforeBaby == null && afterBaby != null) ||
                        (beforeBaby != null && afterBaby == null) ||
                        (beforeBaby != null && afterBaby != null && !beforeBaby.id().equals(afterBaby.id()));

        if (changed) {
            // Quitar el que estuviera (en todas las dimensiones por seguridad)
            com.jvprz.spectrobesreforged.content.prizmod.SpectrobeManager
                    .despawnBabyEverywhere(player.server, player);

            // Si ahora hay bebé equipado, spawnearlo cerca del jugador (en su dimensión actual)
            if (afterBaby != null) {
                com.jvprz.spectrobesreforged.content.prizmod.SpectrobeManager
                        .spawnBaby(player.serverLevel(), player, afterBaby);
            }
        }

        com.jvprz.spectrobesreforged.network.ModSnapshotSender.sendSnapshot(player, data);

        ModSnapshotSender.sendSnapshot(player, data);
    }
}