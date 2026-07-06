package com.jvprz.spectrobesreforged.client.model;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KrawlModel extends GeoModel<KrawlEntity> {

    @Override
    public ResourceLocation getModelResource(KrawlEntity entity) {
        String key = entity.getDefinition().key();

        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "geo/entity/krawl/" + key + ".geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(KrawlEntity entity) {
        String key = entity.getDefinition().key();

        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "textures/entity/krawl/" + key + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(KrawlEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "animations/entity/krawl/" +
                        entity.getDefinition().key() +
                        ".animation.json"
        );
    }
}