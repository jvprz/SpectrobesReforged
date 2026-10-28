package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record C2SExcavationCursor(int container,int x,int y,int tool,boolean using) implements CustomPacketPayload {
    public static final Type<C2SExcavationCursor> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"excavation_cursor"));
    public static final StreamCodec<FriendlyByteBuf,C2SExcavationCursor> STREAM_CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.container);b.writeVarInt(p.x);b.writeVarInt(p.y);b.writeVarInt(p.tool);b.writeBoolean(p.using);},b->new C2SExcavationCursor(b.readVarInt(),b.readVarInt(),b.readVarInt(),b.readVarInt(),b.readBoolean()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void handle(C2SExcavationCursor packet,ServerPlayer player){
        if(!player.isAlive()||player.isSpectator()||!PlayerRequestLimits.allow(player,"excavation_cursor",2))return;
        if(player.containerMenu instanceof ExcavationMenu menu&&menu.containerId==packet.container&&menu.stillValid(player)&&menu.table()!=null)
            menu.table().cursor(player,packet.x,packet.y,packet.tool,packet.using);
    }
}
