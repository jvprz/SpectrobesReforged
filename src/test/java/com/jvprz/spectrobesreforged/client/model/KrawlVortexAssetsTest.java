package com.jvprz.spectrobesreforged.client.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter;
import software.bernie.geckolib.loading.object.BakedAnimations;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class KrawlVortexAssetsTest {
    private JsonObject load(String path) throws Exception {
        try (var stream = getClass().getResourceAsStream("/assets/spectrobesreforged/" + path)) {
            assertNotNull(stream, path);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    @Test void geckoLibCanLoadGeometryAndAllThreeAnimations() throws Exception {
        var geometry = load("geo/entity/krawl/vortex.geo.json");
        var model = KeyFramesAdapter.GEO_GSON.fromJson(geometry, Model.class);
        var bones = model.minecraftGeometry()[0].bones();
        assertEquals(12, Arrays.stream(bones).mapToInt(b -> b.cubes().length).sum());
        var names = Arrays.stream(bones).map(b -> b.name()).collect(Collectors.toSet());
        assertTrue(names.containsAll(Set.of("vortex_root", "spiral_body", "cloud_crown")));
        var json = load("animations/entity/krawl/vortex.animation.json");
        var animations = KeyFramesAdapter.GEO_GSON.fromJson(json.getAsJsonObject("animations"), BakedAnimations.class);
        assertEquals(Set.of("idle", "chase", "catch"), animations.animations().keySet());
        for (var entry : json.getAsJsonObject("animations").entrySet()) {
            assertTrue(names.containsAll(entry.getValue().getAsJsonObject().getAsJsonObject("bones").keySet()));
        }
    }

    @Test void everyFaceRetainsSquareTexelsAfterExport() throws Exception {
        var geometry = load("geo/entity/krawl/vortex.geo.json")
                .getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject();
        int count = 0;
        for (var bone : geometry.getAsJsonArray("bones")) {
            if (!bone.getAsJsonObject().has("cubes")) continue;
            for (var element : bone.getAsJsonObject().getAsJsonArray("cubes")) {
                var cube = element.getAsJsonObject();
                var dimensions = cube.getAsJsonArray("size");
                double x = dimensions.get(0).getAsDouble(), y = dimensions.get(1).getAsDouble(), z = dimensions.get(2).getAsDouble();
                for (var entry : cube.getAsJsonObject("uv").entrySet()) {
                    var uv = entry.getValue().getAsJsonObject().getAsJsonArray("uv_size");
                    double w = switch (entry.getKey()) { case "east", "west" -> z; default -> x; };
                    double h = switch (entry.getKey()) { case "up", "down" -> z; default -> y; };
                    assertEquals(2, Math.abs(uv.get(0).getAsDouble()) / w, 0.00001);
                    assertEquals(2, Math.abs(uv.get(1).getAsDouble()) / h, 0.00001);
                    count++;
                }
            }
        }
        assertEquals(72, count);
    }
}
