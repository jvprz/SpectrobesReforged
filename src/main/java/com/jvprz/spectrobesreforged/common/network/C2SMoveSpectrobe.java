package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.*;
import com.jvprz.spectrobesreforged.common.feature.prizmod.logic.SpectrobeManager;
import com.jvprz.spectrobesreforged.common.feature.prizmod.menu.PrizmodMenu;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public record C2SMoveSpectrobe(int fromType,int fromIndex,int toType,int toIndex,UUID expected) implements CustomPacketPayload {
    public static final Type<C2SMoveSpectrobe> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"move_spectrobe"));
    public C2SMoveSpectrobe(int from,int fromIndex,int to,int toIndex){this(from,fromIndex,to,toIndex,null);}
    public static final StreamCodec<FriendlyByteBuf,C2SMoveSpectrobe> STREAM_CODEC=StreamCodec.of((buf,v)->{
        buf.writeVarInt(v.fromType);buf.writeVarInt(v.fromIndex);buf.writeVarInt(v.toType);buf.writeVarInt(v.toIndex);buf.writeBoolean(v.expected!=null);if(v.expected!=null)buf.writeUUID(v.expected);
    },buf->new C2SMoveSpectrobe(buf.readVarInt(),buf.readVarInt(),buf.readVarInt(),buf.readVarInt(),buf.readBoolean()?buf.readUUID():null));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void handle(C2SMoveSpectrobe message,ServerPlayer player){
        if(!(player.containerMenu instanceof PrizmodMenu menu)||!menu.stillValid(player)||message.expected==null
                ||!PlayerRequestLimits.allow(player,"prizmod_move",2))return;
        var data=player.getData(ModAttachments.PRIZMOD.get());var before=data.getBabySlot().map(SpectrobeEntry::id).orElse(null);
        boolean moved=PrizmodTransfers.move(data,message.fromType,message.fromIndex,message.toType,message.toIndex,message.expected);
        var after=data.getBabySlot().map(SpectrobeEntry::id).orElse(null);
        if(moved&&!java.util.Objects.equals(before,after)){
            SpectrobeManager.despawnBabyEverywhere(player.server,player);
            data.getBabySlot().ifPresent(entry->SpectrobeManager.spawnBaby(player.serverLevel(),player,entry));
        }
        // Also refresh a rejected stale selection; the client never moves records optimistically.
        ModSnapshotSender.sendSnapshot(player,data);
    }
}
