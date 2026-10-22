package com.jvprz.spectrobesreforged.localization;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

class LanguageResourcesTest {
    private JsonObject load(String locale) throws Exception {
        try (var stream = getClass().getResourceAsStream(
                "/assets/spectrobesreforged/lang/" + locale + ".json")) {
            assertNotNull(stream, locale);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
        }
    }

    @Test
    void translationsHaveCompleteKeysAndMatchingArguments() throws Exception {
        var english = load("en_us");
        var placeholders = Pattern.compile("%[sd]");
        for (String locale : new String[]{"es_es", "fr_fr", "de_de", "it_it", "pt_br"}) {
            var translated = load(locale);
            assertEquals(english.keySet(), translated.keySet(), locale);
            for (String key : english.keySet()) {
                String value = translated.get(key).getAsString();
                assertFalse(value.isBlank(), locale + ": " + key);
                assertEquals(placeholders.matcher(english.get(key).getAsString()).results()
                                .map(result -> result.group()).toList(),
                        placeholders.matcher(value).results().map(result -> result.group()).toList(),
                        locale + ": " + key);
            }
        }
    }
}
