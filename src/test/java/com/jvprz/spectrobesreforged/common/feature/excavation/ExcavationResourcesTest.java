package com.jvprz.spectrobesreforged.common.feature.excavation;

import com.google.gson.*;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class ExcavationResourcesTest {
    private JsonObject resource(String path) throws Exception {
        try(var stream=getClass().getResourceAsStream("/"+path)){
            assertNotNull(stream,path);
            return JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
    @Test void allEightMineralOresKeepSilkTouchAndFixThreePossibleResultsInRawDrops() throws Exception {
        for(String grade:new String[]{"c","b","a","a_plus"})for(String prefix:new String[]{"","deepslate_"}){
            String ore=prefix+grade+"_mineral_ore";
            var pools=resource("data/spectrobesreforged/loot_table/blocks/"+ore+".json").getAsJsonArray("pools");
            assertEquals("spectrobesreforged:"+ore,pools.get(0).getAsJsonObject().getAsJsonArray("entries").get(0).getAsJsonObject().get("name").getAsString());
            var raw=pools.get(1).getAsJsonObject().getAsJsonArray("entries");assertEquals(3,raw.size());
            for(int i=0;i<raw.size();i++){
                var entry=raw.get(i).getAsJsonObject();assertEquals("spectrobesreforged:raw_"+grade+"_mineral",entry.get("name").getAsString());
                var function=entry.getAsJsonArray("functions").get(0).getAsJsonObject();assertEquals("minecraft:set_custom_data",function.get("function").getAsString());
                String result=new String[]{"power","defense","health"}[i]+"_"+grade;
                assertEquals("{Mineral:\""+result+"\"}",function.get("tag").getAsString());assertNotNull(ExcavationMasks.get(result));
            }
        }
    }
    @Test void allPortableMineralsHaveMatchingModelsAndTextureAssets() throws Exception {
        for(String grade:new String[]{"c","b","a","a_plus"}){
            String name="raw_"+grade+"_mineral";
            var model=resource("assets/spectrobesreforged/models/item/"+name+".json");
            assertEquals("spectrobesreforged:item/"+name,model.getAsJsonObject("textures").get("layer0").getAsString());
            try(var stream=getClass().getResourceAsStream("/assets/spectrobesreforged/textures/item/"+name+".png")){assertNotNull(stream);assertTrue(stream.readAllBytes().length>50);}
        }
    }
}
