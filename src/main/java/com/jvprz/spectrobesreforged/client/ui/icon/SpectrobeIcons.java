package com.jvprz.spectrobesreforged.client.ui.icon;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.minecraft.resources.ResourceLocation;

public final class SpectrobeIcons {
    private SpectrobeIcons() {}

    public static ResourceLocation icon(String species) {
        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "gui/icons/" + species.toLowerCase() + ".png"
        );
    }
}