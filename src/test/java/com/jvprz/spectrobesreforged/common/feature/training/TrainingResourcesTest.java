package com.jvprz.spectrobesreforged.common.feature.training;
import com.google.gson.*;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;
class TrainingResourcesTest {
 @Test void advancementTreeAndTranslationsAreComplete() throws Exception {
  Path root=Path.of("src/main/resources"),folder=root.resolve("data/spectrobesreforged/advancement");
  var lang=JsonParser.parseString(Files.readString(root.resolve("assets/spectrobesreforged/lang/en_us.json"))).getAsJsonObject();
  try(var paths=Files.list(folder)){for(var path:paths.filter(p->p.toString().endsWith(".json")).toList()){
   var json=JsonParser.parseString(Files.readString(path)).getAsJsonObject();if(json.has("parent")){String parent=json.get("parent").getAsString();if(parent.startsWith("spectrobesreforged:"))assertTrue(Files.isRegularFile(folder.resolve(parent.split(":")[1]+".json")),parent);}
   if(json.has("display"))for(String key:new String[]{"title","description"})assertTrue(lang.has(json.getAsJsonObject("display").getAsJsonObject(key).get("translate").getAsString()),path.toString());
  }}
  var variety=JsonParser.parseString(Files.readString(folder.resolve("excavation_variety.json"))).getAsJsonObject();assertEquals(6,variety.getAsJsonObject("criteria").size());assertEquals(6,variety.getAsJsonArray("requirements").size());for(var clause:variety.getAsJsonArray("requirements"))assertEquals(1,clause.getAsJsonArray().size());
 }
}
