package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.item.MineralItem;
import com.jvprz.spectrobesreforged.common.feature.prizmod.menu.PrizmodMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public record C2SFeedSpectrobe(UUID spectrobe,int slot,int expectedItem) implements CustomPacketPayload {
    public static final Type<C2SFeedSpectrobe> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"feed_spectrobe"));
    public static final StreamCodec<FriendlyByteBuf,C2SFeedSpectrobe> STREAM_CODEC=StreamCodec.of((buf,v)->{buf.writeUUID(v.spectrobe);buf.writeVarInt(v.slot);buf.writeVarInt(v.expectedItem);},buf->new C2SFeedSpectrobe(buf.readUUID(),buf.readVarInt(),buf.readVarInt()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void handle(C2SFeedSpectrobe message,ServerPlayer player){
        if(!(player.containerMenu instanceof PrizmodMenu menu)||!menu.stillValid(player)||message.slot<0||message.slot>=36)return;
        var stack=player.getInventory().getItem(message.slot);
        if(stack.isEmpty()||BuiltInRegistries.ITEM.getId(stack.getItem())!=message.expectedItem||!(stack.getItem() instanceof MineralItem mineral))return;
        long now=player.serverLevel().getGameTime();var tag=player.getPersistentData();
        if(tag.contains("PrizmodFeedTick")&&now-tag.getLong("PrizmodFeedTick")<2)return;
        tag.putLong("PrizmodFeedTick",now);
        mineral.feedTo(player,message.spectrobe,stack,null);
        player.getInventory().setChanged();
        player.connection.send(new ClientboundContainerSetSlotPacket(-2,0,message.slot,stack.copy()));
    }
}
