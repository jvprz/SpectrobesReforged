package com.jvprz.spectrobesreforged.common.feature.spectrobe.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class SpectrobeSpeciesLoaderTest {
    @AfterEach
    void cleanRegistry() { SpectrobeSpeciesRegistry.clear(); }

    @Test
    void reloadReplacesChangedSpeciesAndRemovesDeletedSpecies() throws Exception {
        var loader = new SpectrobeSpeciesLoader();
        var id = ResourceLocation.fromNamespaceAndPath("spectrobesreforged", "inkana");
        try (var stream = getClass().getResourceAsStream(
                "/data/spectrobesreforged/entity/spectrobe/inkana.json")) {
            assertNotNull(stream);
            var json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            loader.apply(Map.of(id, json), null, null);
            assertNotNull(SpectrobeSpeciesRegistry.getByKey("inkana"));
            json.addProperty("range", 9);
            loader.apply(Map.of(id, json), null, null);
            assertEquals(9, SpectrobeSpeciesRegistry.getByKey("inkana").range());
            loader.apply(Map.of(), null, null);
            assertTrue(SpectrobeSpeciesRegistry.all().isEmpty());
        }
    }

    @Test
    void malformedRootIsReportedWithoutAbortingOtherDefinitions() {
        var loader = new SpectrobeSpeciesLoader();
        Map<ResourceLocation, JsonElement> invalid = Map.of(
                ResourceLocation.fromNamespaceAndPath("spectrobesreforged", "bad"),
                JsonParser.parseString("[]"));
        assertDoesNotThrow(() -> loader.apply(invalid, null, null));
        assertTrue(SpectrobeSpeciesRegistry.all().isEmpty());
    }
    @Test
    void childRequirementsMatchBeyondThePortalsAndHavePreviewAssets() throws Exception {
        var expected=Map.of("komainu",5,"inkana",4,"vilar",7,"segu",4,"spiko",3,"shakin",4);
        for(var entry:expected.entrySet()) {
            String key=entry.getKey();
            try(var stream=getClass().getResourceAsStream("/data/spectrobesreforged/entity/spectrobe/"+key+".json")) {
                assertNotNull(stream);
                var json=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
                assertEquals(entry.getValue().intValue(),json.getAsJsonObject("evolution").get("minerals_required").getAsInt(),key);
                if(key.equals("shakin"))assertEquals("shankor",json.getAsJsonObject("evolution").get("to").getAsString());
            }
            for(String path:new String[]{"geo/entity/spectrobe/"+key+".geo.json","animations/entity/spectrobe/"+key+".animation.json"})
                assertNotNull(getClass().getResource("/assets/spectrobesreforged/"+path),path);
            assertNotNull(getClass().getResource("/assets/spectrobesreforged/textures/entity/spectrobe/"+key+"/"+key+"_color_0.png"),key);
        }
    }
}
