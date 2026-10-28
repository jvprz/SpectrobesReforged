package com.jvprz.spectrobesreforged.common.feature.spectrobe.loader;
import com.google.gson.*;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeStage;
import com.jvprz.spectrobesreforged.common.feature.excavation.*;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class ChildRosterTest {
 private final Path resources=Path.of("src/main/resources");
 private JsonObject json(Path p)throws Exception{return JsonParser.parseString(Files.readString(p)).getAsJsonObject();}
 private List<JsonObject> definitions()throws Exception{
  try(var files=Files.list(resources.resolve("data/spectrobesreforged/entity/spectrobe"))){var result=new ArrayList<JsonObject>();for(var p:files.filter(p->p.toString().endsWith(".json")).toList())result.add(json(p));return result;}
 }
 @AfterEach void reset(){SpectrobeSpeciesRegistry.clear();}
 @Test void allDefinitionsLoadAndHaveUniqueDatabaseIds()throws Exception{
  var input=new HashMap<ResourceLocation,JsonElement>();var ids=new HashSet<Integer>();
  for(var d:definitions()){assertTrue(ids.add(d.get("id").getAsInt()));input.put(ResourceLocation.fromNamespaceAndPath("spectrobesreforged",d.get("key").getAsString()),d);}
  new SpectrobeSpeciesLoader().apply(input,null,null);assertEquals(input.size(),SpectrobeSpeciesRegistry.all().size());
  for(var d:definitions()){var s=SpectrobeSpeciesRegistry.getByKey(d.get("key").getAsString());assertNotNull(s);assertEquals(SpectrobeStage.CHILD,s.initialStage());assertTrue(s.stats().hp().base()>0);}
 }
 @Test void everyPlayableChildHasVisualsAnimationsAndAnExcavationMask()throws Exception{
  Path assets=resources.resolve("assets/spectrobesreforged");
  for(var d:definitions()){
   String key=d.get("key").getAsString();assertNotNull(ExcavationMasks.get(key),key);
   assertTrue(Files.isRegularFile(assets.resolve("textures/item/fossil/"+key+"_fossil.png")),key);
   assertTrue(Files.isRegularFile(assets.resolve("textures/gui/icons/"+key+".png")),key);
   assertTrue(Files.isRegularFile(assets.resolve("textures/entity/spectrobe/"+key+"/"+key+"_color_0.png")),key);
   var animation=json(assets.resolve("animations/entity/spectrobe/"+key+".animation.json")).getAsJsonObject("animations");for(String state:new String[]{"idle","walk","sniff"})assertTrue(animation.has(state),key+state);
   assertFalse(json(assets.resolve("geo/entity/spectrobe/"+key+".geo.json")).getAsJsonArray("minecraft:geometry").isEmpty());
  }
 }
 @Test void temporaryModelsAndTheirOwnAnimationsStayCompatible()throws Exception{
  Path assets=resources.resolve("assets/spectrobesreforged");
  for(var d:definitions())if(d.has("model_status")){
   String key=d.get("key").getAsString();
   var geometry=json(assets.resolve("geo/entity/spectrobe/"+key+".geo.json")).getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject();
   Set<String> bones=new HashSet<>();
   for(var bone:geometry.getAsJsonArray("bones"))bones.add(bone.getAsJsonObject().get("name").getAsString());
   var animations=json(assets.resolve("animations/entity/spectrobe/"+key+".animation.json")).getAsJsonObject("animations");
   for(var animation:animations.entrySet()){
    var animatedBones=animation.getValue().getAsJsonObject().getAsJsonObject("bones");
    if(animatedBones!=null)for(String bone:animatedBones.keySet())assertTrue(bones.contains(bone),key+": "+bone);
   }
   for(int color=0;color<3;color++){
    var image=javax.imageio.ImageIO.read(assets.resolve("textures/entity/spectrobe/"+key+"/"+key+"_color_"+color+".png").toFile());
    assertNotNull(image,key);assertEquals(geometry.getAsJsonObject("description").get("texture_width").getAsInt(),image.getWidth(),key);
    assertEquals(geometry.getAsJsonObject("description").get("texture_height").getAsInt(),image.getHeight(),key);
   }
  }
 }
 @Test void completeRosterCanBeRecoveredManuallyAndHasMatchingElementLoot()throws Exception{
  for(var d:definitions()){
   String key=d.get("key").getAsString(),element=d.get("type").getAsString();
   var puzzle=new ExcavationPuzzle(ExcavationMasks.get(key),42);for(int pass=0;pass<7;pass++)for(int y=0;y<32;y+=3)for(int x=0;x<32;x+=3){puzzle.work(x,y,0);puzzle.work(x,y,1);}assertTrue(puzzle.canExtract(),key);assertEquals(100,puzzle.integrity(),key);
   var loot=json(resources.resolve("data/spectrobesreforged/loot_table/blocks/"+element+"_fossil_ore.json"));assertTrue(loot.getAsJsonArray("pools").get(1).getAsJsonObject().getAsJsonArray("entries").asList().stream().anyMatch(e->e.getAsJsonObject().getAsJsonArray("functions").get(0).getAsJsonObject().get("tag").getAsString().contains("Fossil:\""+key+"\"")),key);
  }
 }
 @Test void runtimeModelsAreSpeciesSpecificAndGenericTemplatesAreNotPackaged()throws Exception{
  for(String path:new String[]{"geo/entity/spectrobe/default.geo.json","animations/entity/spectrobe/default.animation.json","textures/entity/spectrobe/dafault.png"})
   assertNull(getClass().getResource("/assets/spectrobesreforged/"+path),path);
  for(var definition:definitions()){
   String key=definition.get("key").getAsString();
   if(!definition.has("model_status"))continue;
   var model=json(resources.resolve("assets/spectrobesreforged/geo/entity/spectrobe/"+key+".geo.json"));
   assertEquals("geometry."+key,model.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject().getAsJsonObject("description").get("identifier").getAsString());
   for(var texture:definition.getAsJsonArray("textures"))assertTrue(texture.getAsString().startsWith("entity/spectrobe/"+key+"/"),key);
  }
 }
 @Test void individualPlaceholderGeometryAndAnimationsCanBeBakedByGeckoLib()throws Exception{
  Path assets=resources.resolve("assets/spectrobesreforged");
  for(var definition:definitions())if(definition.has("model_status")){
   String key=definition.get("key").getAsString();
   var model=software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter.GEO_GSON.fromJson(
    json(assets.resolve("geo/entity/spectrobe/"+key+".geo.json")),software.bernie.geckolib.loading.json.raw.Model.class);
   assertTrue(model.minecraftGeometry()[0].bones().length>0,key);
   var animations=software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter.GEO_GSON.fromJson(
    json(assets.resolve("animations/entity/spectrobe/"+key+".animation.json")).getAsJsonObject("animations"),
    software.bernie.geckolib.loading.object.BakedAnimations.class);
   assertTrue(animations.animations().keySet().containsAll(Set.of("idle","walk","sniff")),key);
  }
 }
}
