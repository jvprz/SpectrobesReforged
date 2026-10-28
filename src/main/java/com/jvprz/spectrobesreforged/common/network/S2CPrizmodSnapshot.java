package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.*;

public record S2CPrizmodSnapshot(List<Entry> box,List<Entry> team,Entry baby) implements CustomPacketPayload {
    private static final int WIRE_VERSION=5;
    public static final Type<S2CPrizmodSnapshot> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"prizmod_snapshot"));
    public record Entry(UUID id,String species,int color,String stage,int level,int hp,int hpCur,int atk,int def,
                        int mineralsFed,int mineralHpBonus,int mineralAtkBonus,int mineralDefBonus,SpectrobeProgress progress,String element) {
    }
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static final StreamCodec<FriendlyByteBuf,S2CPrizmodSnapshot> STREAM_CODEC=new StreamCodec<>() {
        private int size(FriendlyByteBuf buf,int max){int count=buf.readVarInt();if(count<0||count>max)throw new IllegalArgumentException("Invalid Prizmod collection size");return count;}
        @Override public S2CPrizmodSnapshot decode(FriendlyByteBuf buf){
            int start=buf.readerIndex(),version=buf.readVarInt();
            if(version==4||version==5)return read(buf,version);
            try{if(version==2||version==3)return read(buf,version);}catch(RuntimeException malformedLegacy){}
            buf.readerIndex(start);return read(buf,1);
        }
        private S2CPrizmodSnapshot read(FriendlyByteBuf buf,int version){
            int count=size(buf,16384);List<Entry> box=new ArrayList<>(count);for(int i=0;i<count;i++)box.add(entry(buf,version));
            int teamCount=size(buf,6);List<Entry> team=new ArrayList<>(teamCount);for(int i=0;i<teamCount;i++)team.add(buf.readBoolean()?entry(buf,version):null);
            return new S2CPrizmodSnapshot(box,team,buf.readBoolean()?entry(buf,version):null);
        }
        private Entry entry(FriendlyByteBuf buf,int version){
            UUID id=buf.readUUID();String species=buf.readUtf(256);
            if(version==1)return new Entry(id,species,0,buf.readBoolean()?"CHILD":"ADULT",1,0,0,0,0,0,0,0,0,SpectrobeProgress.EMPTY,"unknown");
            int color=buf.readVarInt();String stage=buf.readUtf(32);int level=buf.readVarInt(),hp=buf.readVarInt(),cur=buf.readVarInt(),atk=buf.readVarInt(),def=buf.readVarInt();
            int fed=0,hb=0,ab=0,db=0;
            if(version>=3){fed=buf.readVarInt();hb=buf.readVarInt();ab=buf.readVarInt();db=buf.readVarInt();}
            SpectrobeProgress progress=version>=4?new SpectrobeProgress(buf.readVarInt(),buf.readBoolean(),buf.readVarInt(),buf.readVarInt()):SpectrobeProgress.EMPTY;
            if(version>=5&&buf.readBoolean())progress=progress.withCooperative();
            return new Entry(id,species,color,stage,level,hp,cur,atk,def,fed,hb,ab,db,progress,version>=4?buf.readUtf(32):"unknown");
        }
        @Override public void encode(FriendlyByteBuf buf,S2CPrizmodSnapshot value){
            buf.writeVarInt(WIRE_VERSION);buf.writeVarInt(value.box.size());value.box.forEach(e->write(buf,e));
            buf.writeVarInt(value.team.size());value.team.forEach(e->{buf.writeBoolean(e!=null);if(e!=null)write(buf,e);});
            buf.writeBoolean(value.baby!=null);if(value.baby!=null)write(buf,value.baby);
        }
        private void write(FriendlyByteBuf buf,Entry e){
            buf.writeUUID(e.id);buf.writeUtf(e.species);buf.writeVarInt(e.color);buf.writeUtf(e.stage);buf.writeVarInt(e.level);
            buf.writeVarInt(e.hp);buf.writeVarInt(e.hpCur);buf.writeVarInt(e.atk);buf.writeVarInt(e.def);buf.writeVarInt(e.mineralsFed);
            buf.writeVarInt(e.mineralHpBonus);buf.writeVarInt(e.mineralAtkBonus);buf.writeVarInt(e.mineralDefBonus);
            buf.writeVarInt(e.progress.xp());buf.writeBoolean(e.progress.crown());buf.writeVarInt(e.progress.quality());buf.writeVarInt(e.progress.battles());buf.writeBoolean(e.progress.cooperative());buf.writeUtf(e.element);
        }
    };
}
