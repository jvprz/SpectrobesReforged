package com.jvprz.spectrobesreforged.common.feature.spectrobe.animation;

import com.google.gson.*;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompanionAnimationResourcesTest {
    private static final Path ASSETS=Path.of("src/main/resources/assets/spectrobesreforged");
    private static JsonObject json(Path file)throws Exception{return JsonParser.parseString(Files.readString(file)).getAsJsonObject();}
    @Test void sevenSpeciesContextualClipsBakeAndOnlyReferenceRealBones()throws Exception {
        for(String species:new String[]{"komainu","spiko","vilar","segu","inkana","shakin","zoza"}) {
            var geometry=json(ASSETS.resolve("geo/entity/spectrobe/"+species+".geo.json")).getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject();
            var names=new HashSet<String>();for(var bone:geometry.getAsJsonArray("bones"))names.add(bone.getAsJsonObject().get("name").getAsString());
            var clips=json(ASSETS.resolve("animations/entity/spectrobe/"+species+".animation.json")).getAsJsonObject("animations");
            var baked=software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter.GEO_GSON.fromJson(clips,software.bernie.geckolib.loading.object.BakedAnimations.class);
            assertTrue(baked.animations().keySet().containsAll(Set.of("komainu","spiko").contains(species)?Set.of("idle","walk","run","sniff","curious","interested","happy","eat","blink","cry","jump"):Set.of("idle","walk","sniff","curious","interested","carry","happy","eat")),species);
            for(var clip:clips.entrySet())assertTrue(names.containsAll(clip.getValue().getAsJsonObject().getAsJsonObject("bones").keySet()),species+":"+clip.getKey());
        }
    }
    @Test void shakinFinsInheritTheBodyAndItsAuthoredMeshIsPreserved()throws Exception {
        var now=json(ASSETS.resolve("geo/entity/spectrobe/shakin.geo.json")).getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject().getAsJsonArray("bones");
        var before=json(Path.of("art/animation-pass/source/shakin.geo.json")).getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject().getAsJsonArray("bones");
        for(var current:now){var bone=current.getAsJsonObject();String name=bone.get("name").getAsString();
            var old=before.asList().stream().map(JsonElement::getAsJsonObject).filter(b->b.get("name").getAsString().equals(name)).findFirst().orElseThrow();
            assertEquals(old.get("cubes"),bone.get("cubes"),"Geometry and UVs must stay unchanged");
            if(name.startsWith("leg_"))assertEquals("body",bone.get("parent").getAsString());
        }
    }
}
