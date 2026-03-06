package com.jvprz.spectrobesreforged.common.feature.spectrobe.loader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;

public class SpectrobeSpeciesLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();

    public SpectrobeSpeciesLoader() {
        super(GSON, "spectrobes");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {

        SpectrobesReforged.LOGGER.info("Loading Spectrobe species...");

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {

            ResourceLocation id = entry.getKey();
            JsonObject json = entry.getValue().getAsJsonObject();

            try {

                int idNum = json.get("id").getAsInt();
                String key = json.get("key").getAsString();

                SpectrobeType type =
                        SpectrobeType.valueOf(json.get("type").getAsString().toUpperCase());

                SpectrobeStage stage =
                        SpectrobeStage.valueOf(json.get("stage").getAsString().toUpperCase());

                int range = json.get("range").getAsInt();

                JsonObject stats = json.getAsJsonObject("stats");

                JsonObject hp = stats.getAsJsonObject("hp");
                JsonObject atk = stats.getAsJsonObject("attack");
                JsonObject def = stats.getAsJsonObject("defense");

                StatGrowth hpStat = new StatGrowth(
                        hp.get("base").getAsInt(),
                        hp.get("max").getAsInt(),
                        hp.get("increment").getAsInt()
                );

                StatGrowth atkStat = new StatGrowth(
                        atk.get("base").getAsInt(),
                        atk.get("max").getAsInt(),
                        atk.get("increment").getAsInt()
                );

                StatGrowth defStat = new StatGrowth(
                        def.get("base").getAsInt(),
                        def.get("max").getAsInt(),
                        def.get("increment").getAsInt()
                );

                int moveSpeed = stats.get("move_speed").getAsInt();
                int chargeSpeed = stats.get("charge_speed").getAsInt();

                JsonObject support = stats.getAsJsonObject("support");

                SupportStats supportStats = new SupportStats(
                        support.get("attack").getAsInt(),
                        support.get("defense").getAsInt(),
                        support.get("speed").getAsInt(),
                        support.get("charge").getAsInt()
                );

                SpectrobeStats spectrobeStats =
                        new SpectrobeStats(hpStat, atkStat, defStat, moveSpeed, chargeSpeed, supportStats);

                JsonObject evolutionJson = json.getAsJsonObject("evolution");

                SpectrobeSpecies.EvolutionData evolution =
                        new SpectrobeSpecies.EvolutionData(
                                evolutionJson.get("to").getAsString(),
                                evolutionJson.get("minerals_required").getAsInt()
                        );

                List<String> textures =
                        GSON.fromJson(json.get("textures"), List.class);

                SpectrobeSpecies species = new SpectrobeSpecies(
                        idNum,
                        key,
                        type,
                        stage,
                        range,
                        spectrobeStats,
                        evolution,
                        textures
                );

                SpectrobeSpeciesRegistry.register(species);

                SpectrobesReforged.LOGGER.info("Loaded spectrobe: {}", key);

            } catch (Exception e) {
                SpectrobesReforged.LOGGER.error("Failed loading spectrobe: " + id, e);
            }
        }

        SpectrobesReforged.LOGGER.info("Spectrobe species loaded: {}", jsonMap.size());
    }
}