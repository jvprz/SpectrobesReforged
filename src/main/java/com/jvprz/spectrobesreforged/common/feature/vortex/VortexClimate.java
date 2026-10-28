package com.jvprz.spectrobesreforged.common.feature.vortex;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class VortexClimate {
    private VortexClimate() {}
    private static TagKey<Biome> tag(String name) {
        return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, name));
    }
    private static final TagKey<Biome> HOT = tag("hot");
    private static final TagKey<Biome> TEMPERATE = tag("temperate");
    private static final TagKey<Biome> COLD = tag("cold");

    public static String krawl(Holder<Biome> biome) {
        return VortexRules.krawl(biome.is(HOT), biome.is(TEMPERATE), biome.is(COLD), biome.value().getBaseTemperature());
    }
}
