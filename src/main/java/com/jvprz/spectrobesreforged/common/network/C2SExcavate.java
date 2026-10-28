package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record C2SExcavate(int containerId, int x, int y, int tool) implements CustomPacketPayload {
    public static final Type<C2SExcavate> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, "excavate"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SExcavate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, C2SExcavate::containerId, ByteBufCodecs.VAR_INT, C2SExcavate::x,
            ByteBufCodecs.VAR_INT, C2SExcavate::y, ByteBufCodecs.VAR_INT, C2SExcavate::tool, C2SExcavate::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void handle(C2SExcavate packet, ServerPlayer player) {
        if(!player.isAlive()||player.isSpectator())return;
        if (player.containerMenu instanceof ExcavationMenu menu && menu.containerId == packet.containerId
                && menu.stillValid(player) && menu.table() != null) menu.table().work(player, packet.x, packet.y, packet.tool);
    }
}
