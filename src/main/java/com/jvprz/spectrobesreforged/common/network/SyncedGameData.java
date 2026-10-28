package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.common.feature.krawl.data.KrawlDefinition;
import java.util.*;

/** Client-side cache with no client-only types; isolated from integrated/dedicated server registries. */
public final class SyncedGameData {
    private SyncedGameData() {}
    private record State(Map<String,Integer> ranges,Map<String,KrawlDefinition> krawl) {}
    private static volatile State state=new State(Map.of(),Map.of());
    public static void apply(S2CGameData.Snapshot snapshot){
        Map<String,KrawlDefinition> krawl=new HashMap<>();
        snapshot.krawl().forEach(def->krawl.put(def.key(),def));
        state=new State(snapshot.ranges(),Map.copyOf(krawl));
    }
    public static int range(String species){return state.ranges.getOrDefault(species,0);}
    public static KrawlDefinition krawl(String key){return state.krawl.getOrDefault(key,state.krawl.get("blova"));}
    public static void clear(){state=new State(Map.of(),Map.of());}
}
