package com.jvprz.spectrobesreforged.common.network;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.jvprz.spectrobesreforged.common.feature.krawl.loader.KrawlParser;
import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class GameDataSyncTest {
    @AfterEach void clear(){SyncedGameData.clear();KrawlRegistry.clear();}
    @Test void dataRoundTripPreservesKrawlAndDoesNotOverwriteServerRegistry()throws Exception{
        var definition=KrawlParser.parse(JsonParser.parseString(Files.readString(Path.of("src/main/resources/data/spectrobesreforged/entity/krawl/blova.json"))).getAsJsonObject());
        KrawlRegistry.register(definition);
        var state=new S2CGameData.Snapshot(Map.of("komainu",3,"harumi",7),List.of(definition));
        var packet=new S2CGameData(new Gson().toJson(state));
        var buffer=new FriendlyByteBuf(Unpooled.buffer());
        try{
            S2CGameData.STREAM_CODEC.encode(buffer,packet);
            var decoded=S2CGameData.STREAM_CODEC.decode(buffer).snapshot();
            assertEquals(state,decoded);SyncedGameData.apply(decoded);
            assertEquals(7,SyncedGameData.range("harumi"));assertEquals(definition,SyncedGameData.krawl("blova"));
            SyncedGameData.clear();assertEquals(0,SyncedGameData.range("harumi"));assertEquals(definition,KrawlRegistry.getByKey("blova"));
        }finally{buffer.release();}
    }
    @Test void invalidOrOversizedDefinitionsAreRejected(){
        assertThrows(IllegalArgumentException.class,()->new S2CGameData("x".repeat(200001)));
        assertThrows(IllegalArgumentException.class,()->new S2CGameData.Snapshot(Map.of("bad/key",3),List.of()));
        assertThrows(IllegalArgumentException.class,()->new S2CGameData.Snapshot(Map.of("komainu",100),List.of()));
    }
}
