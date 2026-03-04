// src/main/java/com/jvprz/spectrobesreforged/common/network/C2SMoveSpectrobe.java
package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.feature.prizmod.logic.SpectrobeManager;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

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

        SpectrobeEntry beforeBaby = data.getBabySlot().orElse(null);

        // Validación básica
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

        // Reglas de slots:
        // - BABY slot (type=2) solo CHILD
        // - TEAM slot (type=1) NO permite CHILD (solo ADULT/EVOLVED)
        boolean isChild = moving.isChild();

        if (msg.toType == 2 && !isChild) return;
        if (msg.toType == 1 && isChild) return;

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

        // Si destino ocupado, lo quitamos del destino y lo devolvemos al origen (swap)
        if (displaced != null) {
            // OJO: si el desplazado no cabe en el origen por reglas, cancelamos y revertimos
            boolean displacedIsChild = displaced.isChild();
            boolean displacedCanGoToOrigin = switch (msg.fromType) {
                case 2 -> displacedIsChild;     // origen baby slot solo CHILD
                case 1 -> !displacedIsChild;    // origen team slot solo NO-CHILD
                default -> true;                // box acepta todo
            };
            if (!displacedCanGoToOrigin) {
                // revertimos el moving al origen y abortamos
                switch (msg.fromType) {
                    case 0 -> data.insertBoxAt(Math.min(msg.fromIndex, data.getBox().size()), moving);
                    case 1 -> data.setTeamSlot(msg.fromIndex, moving);
                    case 2 -> data.setBabySlot(moving);
                }
                return;
            }

            // quitar desplazado del destino
            switch (msg.toType) {
                case 0 -> data.removeFromBox(displaced.id());
                case 1 -> data.setTeamSlot(msg.toIndex, null);
                case 2 -> data.setBabySlot(null);
            }

            // devolver desplazado al origen
            switch (msg.fromType) {
                case 0 -> data.insertBoxAt(Math.min(msg.fromIndex, data.getBox().size()), displaced);
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

        SpectrobeEntry afterBaby = data.getBabySlot().orElse(null);

        boolean babyChanged =
                (beforeBaby == null && afterBaby != null) ||
                        (beforeBaby != null && afterBaby == null) ||
                        (beforeBaby != null && afterBaby != null && !beforeBaby.id().equals(afterBaby.id()));

        if (babyChanged) {
            SpectrobeManager.despawnBabyEverywhere(player.server, player);

            if (afterBaby != null) {
                SpectrobeManager.spawnBaby(player.serverLevel(), player, afterBaby);
            }
        }

        // Solo una vez (tenías duplicado)
        ModSnapshotSender.sendSnapshot(player, data);
    }
}