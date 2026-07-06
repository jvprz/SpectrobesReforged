package com.jvprz.spectrobesreforged.common.feature.krawl.loader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlRegistry;
import com.jvprz.spectrobesreforged.common.feature.krawl.data.KrawlDefinition;
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
                KrawlDefinition krawl = KrawlParser.parse(json);

                KrawlRegistry.register(krawl);

                SpectrobesReforged.LOGGER.info("Loaded krawl: {}", krawl.key());

            } catch (Exception e) {
                SpectrobesReforged.LOGGER.error("Failed loading krawl: " + id, e);
            }
        }

        SpectrobesReforged.LOGGER.info("Krawls loaded: {}", KrawlRegistry.all().size());
    }
}