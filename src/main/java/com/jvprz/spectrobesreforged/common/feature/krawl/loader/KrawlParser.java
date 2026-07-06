package com.jvprz.spectrobesreforged.common.feature.krawl.loader;

import com.google.gson.JsonObject;
import com.jvprz.spectrobesreforged.common.feature.krawl.data.*;

public final class KrawlParser {

    private KrawlParser() {
    }

    public static KrawlDefinition parse(JsonObject json) {
        int id = json.get("id").getAsInt();
        String key = json.get("key").getAsString();
        String name = json.get("name").getAsString();

        KrawlElement element = KrawlElement.valueOf(
                json.get("element").getAsString().toUpperCase()
        );

        KrawlTier tier = KrawlTier.valueOf(
                json.get("rank").getAsString().toUpperCase()
        );

        JsonObject entityJson = json.getAsJsonObject("entity");

        KrawlEntityData entity = new KrawlEntityData(
                entityJson.get("width").getAsFloat(),
                entityJson.get("height").getAsFloat(),
                entityJson.get("eye_height").getAsFloat()
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

        return new KrawlDefinition(
                id,
                key,
                name,
                element,
                tier,
                entity,
                stats,
                ai,
                spawn,
                combat,
                egg
        );
    }
}