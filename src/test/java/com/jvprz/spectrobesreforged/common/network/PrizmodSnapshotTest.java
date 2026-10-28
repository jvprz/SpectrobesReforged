package com.jvprz.spectrobesreforged.common.network;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeProgress;
import net.minecraft.network.FriendlyByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class PrizmodSnapshotTest {
 @Test void newSnapshotPreservesTrainingAndEmptyTeamSlots(){
  var entry=new S2CPrizmodSnapshot.Entry(UUID.randomUUID(),"komainu",2,"CHILD",5,125,100,70,60,12,5,4,3,new SpectrobeProgress(45,true,100,2,true),"corona");
  var packet=new S2CPrizmodSnapshot(List.of(entry),Arrays.asList(null,entry,null,null,null,null),entry);var buf=new FriendlyByteBuf(Unpooled.buffer());
  try{S2CPrizmodSnapshot.STREAM_CODEC.encode(buf,packet);assertEquals(packet,S2CPrizmodSnapshot.STREAM_CODEC.decode(buf));assertEquals(0,buf.readableBytes());}finally{buf.release();}
 }
 @Test void oldVersionThreeUsesNeutralProgress(){
  var buf=new FriendlyByteBuf(Unpooled.buffer());UUID id=UUID.randomUUID();
  try{buf.writeVarInt(3);buf.writeVarInt(1);buf.writeUUID(id);buf.writeUtf("komainu");buf.writeVarInt(0);buf.writeUtf("CHILD");for(int n:new int[]{1,120,100,65,55,2,0,0,0})buf.writeVarInt(n);buf.writeVarInt(0);buf.writeBoolean(false);
   var decoded=S2CPrizmodSnapshot.STREAM_CODEC.decode(buf);assertEquals(id,decoded.box().getFirst().id());assertEquals(SpectrobeProgress.EMPTY,decoded.box().getFirst().progress());assertEquals(2,decoded.box().getFirst().mineralsFed());assertEquals(0,buf.readableBytes());
  }finally{buf.release();}
 }
 @Test void malformedCurrentSnapshotIsRejected(){var buf=new FriendlyByteBuf(Unpooled.buffer());try{buf.writeVarInt(4);buf.writeVarInt(16385);assertThrows(IllegalArgumentException.class,()->S2CPrizmodSnapshot.STREAM_CODEC.decode(buf));}finally{buf.release();}}
}
