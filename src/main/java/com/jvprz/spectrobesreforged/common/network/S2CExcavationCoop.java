package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationCoop;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.*;

public record S2CExcavationCoop(int container,List<ExcavationCoop.Cursor> cursors) implements CustomPacketPayload {
    public S2CExcavationCoop { cursors=List.copyOf(cursors);if(cursors.size()>ExcavationCoop.MAX_VIEWERS)throw new IllegalArgumentException("Too many excavation viewers"); }
    public static final Type<S2CExcavationCoop> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"excavation_coop"));
    public static final StreamCodec<FriendlyByteBuf,S2CExcavationCoop> STREAM_CODEC=StreamCodec.of((b,p)->{
        b.writeVarInt(p.container);b.writeVarInt(p.cursors.size());
        for(var c:p.cursors){b.writeUUID(c.player());b.writeUtf(c.name(),64);b.writeVarInt(c.x());b.writeVarInt(c.y());b.writeVarInt(c.tool());b.writeBoolean(c.using());}
    },b->{
        int container=b.readVarInt(),count=b.readVarInt();if(count<0||count>ExcavationCoop.MAX_VIEWERS)throw new IllegalArgumentException("Invalid cursor count");
        var cursors=new ArrayList<ExcavationCoop.Cursor>();
        for(int i=0;i<count;i++){
            var id=b.readUUID();var name=b.readUtf(64);int x=b.readVarInt(),y=b.readVarInt(),tool=b.readVarInt();boolean using=b.readBoolean();
            if(x< -1||x>=160||y< -1||y>=160||!ExcavationCoop.validTool(tool))throw new IllegalArgumentException("Invalid excavation cursor");
            cursors.add(new ExcavationCoop.Cursor(id,name,x,y,tool,using));
        }return new S2CExcavationCoop(container,cursors);
    });
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
}
