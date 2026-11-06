package com.jvprz.spectrobesreforged.common.feature.training;

import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MedalSpritesTest {
    @Test void hidingNameMedalsOnlyChangesTheDisplayCopy() {
        var original=Component.literal("Komainu").append(" ").append(MedalSprites.perfect()).append(" ").append(MedalSprites.cooperative());
        var hidden=MedalSprites.withoutMedals(original);
        assertEquals("Komainu",hidden.getString().strip());
        assertTrue(original.getString().contains("\uE000"));assertTrue(original.getString().contains("\uE001"));
        assertFalse(hidden.getString().contains("\uE000"));assertFalse(hidden.getString().contains("\uE001"));
    }
}
