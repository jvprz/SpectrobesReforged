package com.jvprz.spectrobesreforged.client.ui.icon;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import java.util.Locale;
import java.util.Set;

public final class SpectrobeIcons {
    private SpectrobeIcons() {}
    private static final Set<String> VARIANTS = Set.of("komainu", "spiko", "vilar", "segu", "inkana", "shakin", "zoza");

    public static ResourceLocation icon(String species, int color) {
        String key = (species == null ? "unknown" : species.trim().toLowerCase(Locale.ROOT));
        int v = Mth.clamp(color, 0, 2);

        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "textures/gui/icons/" + key + (v > 0 && VARIANTS.contains(key) ? "_color_" + v : "") + ".png"
        );
    }
}