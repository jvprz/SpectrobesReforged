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
}
