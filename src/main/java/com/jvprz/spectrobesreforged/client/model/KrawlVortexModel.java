package com.jvprz.spectrobesreforged.client.model;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlVortexEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class KrawlVortexModel extends GeoModel<KrawlVortexEntity> {
    private static ResourceLocation asset(String path) {
        return ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, path);
    }
    @Override public ResourceLocation getModelResource(KrawlVortexEntity entity) {
        return asset("geo/entity/krawl/vortex.geo.json");
    }
    @Override public ResourceLocation getTextureResource(KrawlVortexEntity entity) {
        return asset("textures/entity/krawl/vortex.png");
    }
    @Override public ResourceLocation getAnimationResource(KrawlVortexEntity entity) {
        return asset("animations/entity/krawl/vortex.animation.json");
    }
}
