package com.jvprz.spectrobesreforged.client.ui.icon;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class SpectrobeIcons {
    private SpectrobeIcons() {}

    public static ResourceLocation icon(String species, int color) {
        String key = (species == null ? "unknown" : species.trim().toLowerCase());
        int v = Mth.clamp(color, 0, 2);

        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "textures/gui/icons/" + key + ".png"
        );
    }
}