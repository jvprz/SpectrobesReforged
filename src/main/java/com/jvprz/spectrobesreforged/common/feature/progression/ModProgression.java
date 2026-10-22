package com.jvprz.spectrobesreforged.common.feature.progression;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Grants action criteria only after the corresponding server-side operation succeeds. */
public final class ModProgression {
    private ModProgression() {}

    public static void award(ServerPlayer player, String advancement) {
        award(player, advancement, "completed");
    }

    public static void awakened(ServerPlayer player, String species) {
        award(player, "awakened");
        award(player, "collection", "species_" + species);
    }

    private static void award(ServerPlayer player, String name, String criterion) {
        var holder = player.server.getAdvancements().get(
                ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, name));
        if (holder != null) player.getAdvancements().award(holder, criterion);
    }
}
