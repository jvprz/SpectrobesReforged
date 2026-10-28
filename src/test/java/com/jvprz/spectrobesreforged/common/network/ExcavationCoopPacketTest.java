package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationCoop;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ExcavationCoopPacketTest {
    @Test void cursorsRoundTripWithIdentityToolAndMenu(){
        var packet=new S2CExcavationCoop(17,List.of(new ExcavationCoop.Cursor(UUID.randomUUID(),"Digger",52,104,0,true)));
        var b=new FriendlyByteBuf(Unpooled.buffer());try{S2CExcavationCoop.STREAM_CODEC.encode(b,packet);assertEquals(packet,S2CExcavationCoop.STREAM_CODEC.decode(b));assertEquals(0,b.readableBytes());}finally{b.release();}
    }
    @Test void malformedCursorCountsAndPositionsAreRejected(){
        var b=new FriendlyByteBuf(Unpooled.buffer());try{
            b.writeVarInt(1);b.writeVarInt(17);assertThrows(IllegalArgumentException.class,()->S2CExcavationCoop.STREAM_CODEC.decode(b));b.clear();
            b.writeVarInt(1);b.writeVarInt(1);b.writeUUID(UUID.randomUUID());b.writeUtf("Digger");b.writeVarInt(9999);b.writeVarInt(1);b.writeVarInt(0);b.writeBoolean(true);
            assertThrows(IllegalArgumentException.class,()->S2CExcavationCoop.STREAM_CODEC.decode(b));
        }finally{b.release();}
    }
}
