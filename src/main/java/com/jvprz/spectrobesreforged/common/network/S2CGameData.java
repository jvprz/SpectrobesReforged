package com.jvprz.spectrobesreforged.common.network;

import com.google.gson.Gson;
import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlRegistry;
import com.jvprz.spectrobesreforged.common.feature.krawl.data.KrawlDefinition;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.*;

/** Bounded, server-authoritative display data. Never writes into the server registries on receipt. */
public record S2CGameData(String json) implements CustomPacketPayload {
    private static final int MAX_JSON = 200_000;
    private static final Gson GSON = new Gson();
    public static final Type<S2CGameData> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"game_data"));
    public static final StreamCodec<FriendlyByteBuf,S2CGameData> STREAM_CODEC=StreamCodec.of(
            (buf,packet)->buf.writeUtf(packet.json,MAX_JSON),buf->new S2CGameData(buf.readUtf(MAX_JSON)));
    public S2CGameData { if(json==null||json.length()>MAX_JSON)throw new IllegalArgumentException("Game data exceeds network limit"); }
    public record Snapshot(Map<String,Integer> ranges,List<KrawlDefinition> krawl) {
        public Snapshot {
            if(ranges==null||krawl==null||ranges.size()>1024||krawl.size()>128)throw new IllegalArgumentException("Invalid definition counts");
            ranges=Map.copyOf(ranges);krawl=List.copyOf(krawl);
            ranges.forEach((key,range)->{if(!key.matches("[a-z0-9_]{1,64}")||range<1||range>10)throw new IllegalArgumentException("Invalid scan definition");});
            var keys=new HashSet<String>();
            for(var def:krawl)if(def==null||def.key()==null||!def.key().matches("[a-z0-9_]{1,64}")
                    ||!keys.add(def.key())||def.entity()==null||def.stats()==null||def.ai()==null||def.combat()==null)
                throw new IllegalArgumentException("Invalid Krawl definition");
        }
    }
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public Snapshot snapshot(){return GSON.fromJson(json,Snapshot.class);}
    public static S2CGameData capture(){
        Map<String,Integer> ranges=new TreeMap<>();
        SpectrobeSpeciesRegistry.all().forEach(s->ranges.put(s.key(),s.range()));
        return new S2CGameData(GSON.toJson(new Snapshot(ranges,List.copyOf(KrawlRegistry.all()))));
    }
}
