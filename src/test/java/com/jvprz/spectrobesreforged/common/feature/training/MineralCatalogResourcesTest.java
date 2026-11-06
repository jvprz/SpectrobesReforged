package com.jvprz.spectrobesreforged.common.feature.training;

import com.google.gson.*;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class MineralCatalogResourcesTest {
    @Test void everyNewItemHasAModelAndNamesInAllSixLanguages()throws Exception {
        var root=Path.of("src/main/resources");
        var entries=JsonParser.parseString(Files.readString(root.resolve("data/spectrobesreforged/mineral_catalog.json"))).getAsJsonArray();
        assertEquals(54,entries.size());
        var ids=new HashSet<String>();
        for(var entry:entries){
            var data=entry.getAsJsonObject();String id=data.get("id").getAsString();assertTrue(ids.add(id));
            assertTrue(Files.isRegularFile(root.resolve("assets/spectrobesreforged/models/item/"+id+".json")),id);
            for(String lang:List.of("en_us","es_es","fr_fr","de_de","it_it","pt_br")){
                var text=JsonParser.parseString(Files.readString(root.resolve("assets/spectrobesreforged/lang/"+lang+".json"))).getAsJsonObject();
                assertTrue(text.has("item.spectrobesreforged."+id),lang+":"+id);
            }
            if(data.get("category").getAsString().equals("elemental")) {
                var mask=com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationMasks.get(id);assertNotNull(mask);
                var sprite=javax.imageio.ImageIO.read(root.resolve("assets/spectrobesreforged/textures/item/minerals/"+id+".png").toFile());
                for(int y=0;y<32;y++)for(int x=0;x<32;x++)assertEquals((sprite.getRGB(x/2,y/2)>>>24)>0,mask[y*32+x],id+":"+x+","+y);
            }
        }
        assertEquals(33,entries.asList().stream().filter(e->e.getAsJsonObject().get("prepared").getAsBoolean()).count());
    }
}
