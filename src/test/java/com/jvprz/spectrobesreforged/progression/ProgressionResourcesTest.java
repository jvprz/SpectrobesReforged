package com.jvprz.spectrobesreforged.progression;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

class ProgressionResourcesTest {
    private static final String DATA = "/data/spectrobesreforged/";
    private static final String[] COLORS = {"white", "light_gray", "gray", "black", "brown", "red",
            "orange", "yellow", "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink"};
    private JsonObject load(String path) throws Exception {
        try (var stream = getClass().getResourceAsStream(path)) {
            assertNotNull(stream, path);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    @Test
    void equipmentPatternsUseDefinedIngredientsAndMakeOneItem() throws Exception {
        for (String id : new String[]{"white_prizmod", "incubator"}) {
            var recipe = load(DATA + "recipe/" + id + ".json");
            assertEquals("minecraft:crafting_shaped", recipe.get("type").getAsString());
            assertEquals(3, recipe.getAsJsonArray("pattern").size());
            Set<String> used = new HashSet<>();
            for (var row : recipe.getAsJsonArray("pattern")) {
                assertEquals(3, row.getAsString().length());
                for (char symbol : row.getAsString().toCharArray()) {
                    if (symbol != ' ') used.add(String.valueOf(symbol));
                }
            }
            assertEquals(used, recipe.getAsJsonObject("key").keySet());
            assertEquals("spectrobesreforged:" + id, recipe.getAsJsonObject("result").get("id").getAsString());
            assertEquals(1, recipe.getAsJsonObject("result").get("count").getAsInt());
        }
    }

    @Test
    void everyColorCanBeRecoloredWithoutDuplicatingDevicesAndUnlocksInRecipeBook() throws Exception {
        var tag = load(DATA + "tags/item/prizmods.json").getAsJsonArray("values");
        assertEquals(16, tag.size());
        for (String color : COLORS) {
            String id = "dye_" + color + "_prizmod";
            var recipe = load(DATA + "recipe/" + id + ".json");
            var ingredients = recipe.getAsJsonArray("ingredients");
            assertEquals(2, ingredients.size());
            assertEquals("spectrobesreforged:prizmods", ingredients.get(0).getAsJsonObject().get("tag").getAsString());
            assertEquals("minecraft:" + color + "_dye", ingredients.get(1).getAsJsonObject().get("item").getAsString());
            assertEquals("spectrobesreforged:" + color + "_prizmod", recipe.getAsJsonObject("result").get("id").getAsString());
            assertEquals(1, recipe.getAsJsonObject("result").get("count").getAsInt());
            var unlock = load(DATA + "advancement/recipes/" + id + ".json");
            assertEquals("spectrobesreforged:" + id, unlock.getAsJsonObject("rewards").getAsJsonArray("recipes").get(0).getAsString());
            assertEquals(1, unlock.getAsJsonArray("requirements").size());
            assertEquals(2, unlock.getAsJsonArray("requirements").get(0).getAsJsonArray().size());
        }
    }

    @Test
    void actionMilestonesCannotBeEarnedByMerelyHoldingAnItem() throws Exception {
        for (String action : new String[]{"awakened", "scan", "feed", "chroma"}) {
            var advancement = load(DATA + "advancement/" + action + ".json");
            assertEquals("minecraft:impossible", advancement.getAsJsonObject("criteria")
                    .getAsJsonObject("completed").get("trigger").getAsString());
        }
        var collection = load(DATA + "advancement/collection.json");
        Set<String> expected = Set.of("species_inkana", "species_komainu", "species_segu",
                "species_shakin", "species_spiko", "species_vilar");
        assertEquals(expected, collection.getAsJsonObject("criteria").keySet());
        assertEquals(6, collection.getAsJsonArray("requirements").size());
        for (var requirement : collection.getAsJsonArray("requirements")) {
            assertEquals(1, requirement.getAsJsonArray().size(), "Each species must be required, not OR-ed");
        }
    }
    @Test
    void vortexVictoryIsTheOnlyKrawlCombatMilestone() throws Exception {
        assertNull(getClass().getResource(DATA + "advancement/krawl.json"));
        var advancement = load(DATA + "advancement/seal_vortex.json");
        assertEquals("spectrobesreforged:root", advancement.get("parent").getAsString());
        assertEquals("minecraft:impossible", advancement.getAsJsonObject("criteria")
                .getAsJsonObject("completed").get("trigger").getAsString());
    }

}
