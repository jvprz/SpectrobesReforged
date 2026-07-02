package com.jvprz.spectrobesreforged.common.feature.krawl.loader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.krawl.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class KrawlLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();

    public KrawlLoader() {
        super(GSON, "entity/krawl");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        SpectrobesReforged.LOGGER.info("Loading Krawls...");

        KrawlRegistry.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonObject json = entry.getValue().getAsJsonObject();

            try {
                int idNum = json.get("id").getAsInt();
                String key = json.get("key").getAsString();
                String name = json.get("name").getAsString();

                KrawlElement element = KrawlElement.valueOf(
                        json.get("element").getAsString().toUpperCase()
                );

                KrawlRank rank = KrawlRank.valueOf(
                        json.get("rank").getAsString().toUpperCase()
                );

                JsonObject statsJson = json.getAsJsonObject("stats");

                KrawlStats stats = new KrawlStats(
                        statsJson.get("hp").getAsInt(),
                        statsJson.get("attack").getAsInt(),
                        statsJson.get("defense").getAsInt(),
                        statsJson.get("move_speed").getAsDouble(),
                        statsJson.get("follow_range").getAsDouble(),
                        statsJson.get("attack_speed").getAsInt(),
                        statsJson.get("knockback_resistance").getAsDouble()
                );

                JsonObject aiJson = json.getAsJsonObject("ai");

                KrawlAiData ai = new KrawlAiData(
                        aiJson.get("type").getAsString(),
                        aiJson.get("aggressive").getAsBoolean(),
                        aiJson.get("target_players").getAsBoolean(),
                        aiJson.get("target_spectrobes").getAsBoolean(),
                        aiJson.get("calls_for_help").getAsBoolean(),
                        aiJson.get("break_blocks").getAsBoolean()
                );

                JsonObject spawnJson = json.getAsJsonObject("spawn");
                JsonObject lightJson = spawnJson.getAsJsonObject("light_level");

                KrawlSpawnData spawn = new KrawlSpawnData(
                        spawnJson.get("enabled").getAsBoolean(),
                        spawnJson.get("weight").getAsInt(),
                        spawnJson.get("min_group").getAsInt(),
                        spawnJson.get("max_group").getAsInt(),
                        lightJson.get("min").getAsInt(),
                        lightJson.get("max").getAsInt()
                );

                JsonObject combatJson = json.getAsJsonObject("combat");

                KrawlCombatData combat = new KrawlCombatData(
                        combatJson.get("damage").getAsInt(),
                        combatJson.get("attack_interval").getAsInt()
                );

                JsonObject eggJson = json.getAsJsonObject("egg");

                KrawlEggData egg = new KrawlEggData(
                        eggJson.get("primary").getAsString(),
                        eggJson.get("secondary").getAsString()
                );

                KrawlDefinition krawl = new KrawlDefinition(
                        idNum,
                        key,
                        name,
                        element,
                        rank,
                        stats,
                        ai,
                        spawn,
                        combat,
                        egg
                );

                KrawlRegistry.register(krawl);

                SpectrobesReforged.LOGGER.info("Loaded krawl: {}", key);

            } catch (Exception e) {
                SpectrobesReforged.LOGGER.error("Failed loading krawl: " + id, e);
            }
        }

        SpectrobesReforged.LOGGER.info("Krawls loaded: {}", jsonMap.size());
    }
}