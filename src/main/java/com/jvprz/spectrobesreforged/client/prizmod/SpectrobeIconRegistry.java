package com.jvprz.spectrobesreforged.client.prizmod;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.minecraft.resources.ResourceLocation;

public final class SpectrobeIconRegistry {
    private SpectrobeIconRegistry() {}

    public static ResourceLocation icon(String species) {
        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "textures/gui/icons/" + species.toLowerCase() + ".png"
        );
    }
}