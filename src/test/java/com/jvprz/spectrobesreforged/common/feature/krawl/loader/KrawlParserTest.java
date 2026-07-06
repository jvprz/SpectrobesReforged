package com.jvprz.spectrobesreforged.common.feature.krawl.loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jvprz.spectrobesreforged.common.feature.krawl.data.KrawlDefinition;
import com.jvprz.spectrobesreforged.common.feature.krawl.data.KrawlElement;
import com.jvprz.spectrobesreforged.common.feature.krawl.data.KrawlTier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KrawlParserTest {

    @Test
    void parsesValidKrawl() {

        JsonObject json = JsonParser.parseString("""
        {
          "id": 1,
          "key": "blova",
          "name": "Blova",
          "element": "flash",
          "rank": "basic",

          "entity": {
            "width": 1.2,
            "height": 1.9,
            "eye_height": 0.75
          },

          "stats": {
            "hp": 20,
            "attack": 4,
            "defense": 2,
            "move_speed": 0.28,
            "follow_range": 20,
            "attack_speed": 20,
            "knockback_resistance": 0.0
          },

          "ai": {
            "type": "melee",
            "aggressive": true,
            "target_players": true,
            "target_spectrobes": true,
            "calls_for_help": false,
            "break_blocks": false
          },

          "spawn": {
            "enabled": true,
            "weight": 10,
            "min_group": 1,
            "max_group": 3,
            "light_level": {
              "min": 0,
              "max": 7
            }
          },

          "combat": {
            "damage": 4,
            "attack_interval": 20
          },

          "egg": {
            "primary": "2b0033",
            "secondary": "7b1fa2"
          }
        }
        """).getAsJsonObject();

        KrawlDefinition definition = KrawlParser.parse(json);

        assertNotNull(definition);

        assertEquals(1, definition.id());
        assertEquals("blova", definition.key());
        assertEquals("Blova", definition.name());

        assertEquals(KrawlElement.FLASH, definition.element());
        assertEquals(KrawlTier.BASIC, definition.rank());

        assertEquals(20, definition.stats().hp());
        assertEquals(4, definition.stats().attack());
        assertEquals(2, definition.stats().defense());

        assertTrue(definition.spawn().enabled());
        assertEquals(4, definition.combat().damage());
    }
}