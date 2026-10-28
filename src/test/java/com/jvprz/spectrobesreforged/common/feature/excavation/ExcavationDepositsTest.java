package com.jvprz.spectrobesreforged.common.feature.excavation;

import com.google.gson.*;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ExcavationDepositsTest {
    private JsonObject load(String path)throws Exception {
        try(var stream=getClass().getResourceAsStream("/"+path)){assertNotNull(stream,path);return JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();}
    }
    @Test void eachHostContainsOneFossilAndAllFourGradesInMatchingBiomes()throws Exception {
        assertEquals(20,ExcavationDeposits.all().size());assertEquals(20,ExcavationDeposits.all().stream().map(ExcavationDeposits.Deposit::id).distinct().count());
        for(String host:new String[]{"frozen","sandy","red_sandy","muddy"}){
            var deposits=ExcavationDeposits.all().stream().filter(d->d.host().equals(host)).toList();assertEquals(5,deposits.size());assertEquals(1,deposits.stream().filter(ExcavationDeposits.Deposit::fossil).count());
            assertEquals(Set.of("c","b","a","a_plus"),new HashSet<>(deposits.stream().filter(d->!d.fossil()).map(ExcavationDeposits.Deposit::grade).toList()));
            var modifier=load("data/spectrobesreforged/neoforge/biome_modifier/add_"+host+"_excavation_deposits.json");
            assertEquals("#spectrobesreforged:excavation/"+host,modifier.get("biomes").getAsString());assertEquals(5,modifier.getAsJsonArray("features").size());
            assertFalse(load("data/spectrobesreforged/tags/worldgen/biome/excavation/"+host+".json").getAsJsonArray("values").isEmpty());
            var targets=load("data/spectrobesreforged/tags/block/excavation_hosts/"+host+".json").getAsJsonArray("values");assertFalse(targets.isEmpty());
            for(var value:targets)assertNotEquals("minecraft:stone",value.getAsString());
        }
    }
    @Test void everyDepositHasGenerationLootAndItemResourcesAndKeepsItsGeology()throws Exception {
        for(var d:ExcavationDeposits.all()){
            var feature=load("data/spectrobesreforged/worldgen/configured_feature/"+d.id()+".json");
            assertEquals("spectrobesreforged:excavation_patch",feature.get("type").getAsString());
            var config=feature.getAsJsonObject("config");
            assertEquals("spectrobesreforged:excavation/"+d.host(),config.get("biome_tag").getAsString());
            assertEquals("spectrobesreforged:"+d.id(),config.getAsJsonObject("ore").get("Name").getAsString());
            assertEquals("minecraft:"+Map.of("frozen","packed_ice","sandy","sandstone","red_sandy","red_sandstone","muddy","mud").get(d.host()),config.getAsJsonObject("host").get("Name").getAsString());
            var placed=load("data/spectrobesreforged/worldgen/placed_feature/"+d.id()+".json");assertEquals("spectrobesreforged:"+d.id(),placed.get("feature").getAsString());
            var pools=load("data/spectrobesreforged/loot_table/blocks/"+d.id()+".json").getAsJsonArray("pools");assertEquals("spectrobesreforged:"+d.id(),pools.get(0).getAsJsonObject().getAsJsonArray("entries").get(0).getAsJsonObject().get("name").getAsString());
            for(var entry:pools.get(1).getAsJsonObject().getAsJsonArray("entries")){
                String data=entry.getAsJsonObject().getAsJsonArray("functions").get(0).getAsJsonObject().get("tag").getAsString();
                assertTrue(data.contains("ExcavationTerrain:"+d.terrain()));assertTrue(data.contains("ExcavationRock:"+d.rock()));
                assertTrue(data.contains(d.fossil()?"Fossil:":"Mineral:"));
            }
            load("assets/spectrobesreforged/blockstates/"+d.id()+".json");
            var model=load("assets/spectrobesreforged/models/block/"+d.id()+".json");assertEquals(2,model.getAsJsonArray("elements").size());
            var item=load("assets/spectrobesreforged/models/item/"+d.id()+".json");assertEquals("spectrobesreforged:block/"+d.id(),item.get("parent").getAsString());
        }
    }
    @Test void allNewOresAreMineableWithIronAndIncludedInProgression()throws Exception {
        for(String path:new String[]{"data/minecraft/tags/block/mineable/pickaxe.json","data/minecraft/tags/block/needs_iron_tool.json","data/spectrobesreforged/tags/item/progression_items.json"}){
            var entries=load(path).getAsJsonArray("values");Set<String> names=new HashSet<>();entries.forEach(v->names.add(v.getAsString()));
            for(var d:ExcavationDeposits.all())assertTrue(names.contains("spectrobesreforged:"+d.id()),d.id());
        }
    }
    @Test void patchesUseSurfacePlacementAndStrictBiomeFamilies()throws Exception {
        for(var d:ExcavationDeposits.all()){
            var placed=load("data/spectrobesreforged/worldgen/placed_feature/"+d.id()+".json");
            var modifiers=placed.getAsJsonArray("placement");
            assertEquals("WORLD_SURFACE_WG",modifiers.get(2).getAsJsonObject().get("heightmap").getAsString());
            assertEquals("minecraft:biome",modifiers.get(3).getAsJsonObject().get("type").getAsString());
        }
        var desert=load("data/spectrobesreforged/tags/worldgen/biome/excavation/sandy.json").getAsJsonArray("values");
        assertEquals(1,desert.size());assertEquals("minecraft:desert",desert.get(0).getAsString());
        var red=load("data/spectrobesreforged/tags/worldgen/biome/excavation/red_sandy.json").getAsJsonArray("values");
        assertEquals(Set.of("minecraft:badlands","minecraft:wooded_badlands","minecraft:eroded_badlands"),new HashSet<>(red.asList().stream().map(JsonElement::getAsString).toList()));
        var mud=load("data/spectrobesreforged/tags/worldgen/biome/excavation/muddy.json").getAsJsonArray("values");
        assertEquals(Set.of("minecraft:swamp","minecraft:mangrove_swamp"),new HashSet<>(mud.asList().stream().map(JsonElement::getAsString).toList()));
        var frozen=load("data/spectrobesreforged/tags/worldgen/biome/excavation/frozen.json").getAsJsonArray("values");
        assertFalse(frozen.asList().stream().anyMatch(v->v.getAsString().equals("minecraft:taiga")));
    }
    @Test void patchesCannotReplaceAirFluidsOtherOresOrWood()throws Exception {
        var targets=load("data/spectrobesreforged/tags/block/excavation_patch_replaceable.json").getAsJsonArray("values");
        for(var entry:targets){String id=entry.getAsString();assertFalse(id.contains("ore")||id.contains("log")||id.contains("planks"));assertFalse(Set.of("minecraft:air","minecraft:water","minecraft:lava","minecraft:chest","minecraft:stone_bricks").contains(id));}
    }
}
