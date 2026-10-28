package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.prizmod.PrizmodAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record C2SOpenPrizmod() implements CustomPacketPayload {
    public static final Type<C2SOpenPrizmod> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"open_prizmod"));
    public static final StreamCodec<FriendlyByteBuf,C2SOpenPrizmod> STREAM_CODEC=StreamCodec.unit(new C2SOpenPrizmod());
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void handle(C2SOpenPrizmod packet,ServerPlayer player){
        if(PlayerRequestLimits.allow(player,"open_prizmod",10))PrizmodAccess.open(player);
    }
}
