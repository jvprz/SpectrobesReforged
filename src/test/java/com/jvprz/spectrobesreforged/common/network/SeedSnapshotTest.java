package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeProgress;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SeedSnapshotTest {
    @Test void seedAllowanceSurvivesThePrizmodWireFormat() {
        var progress=new SpectrobeProgress(20,true,100,2,true,3);
        var entry=new S2CPrizmodSnapshot.Entry(UUID.randomUUID(),"komainu",0,"CHILD",4,120,120,65,55,3,0,0,0,progress,"corona");
        var packet=new S2CPrizmodSnapshot(List.of(entry),List.of(),entry);
        var buffer=new FriendlyByteBuf(Unpooled.buffer());
        try{
            S2CPrizmodSnapshot.STREAM_CODEC.encode(buffer,packet);
            assertEquals(packet,S2CPrizmodSnapshot.STREAM_CODEC.decode(buffer));
            assertEquals(0,buffer.readableBytes());
        }finally{buffer.release();}
    }
}
