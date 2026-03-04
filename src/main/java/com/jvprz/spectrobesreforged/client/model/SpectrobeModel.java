package com.jvprz.spectrobesreforged.client.model;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpecies;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpectrobeModel extends GeoModel<SpectrobeEntity> {

    @Override
    public ResourceLocation getModelResource(SpectrobeEntity entity) {
        String key = entity.getSpeciesKey();
        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "geo/" + key + ".geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(SpectrobeEntity entity) {
        String key = entity.getSpeciesKey();          // "komainu"
        int v = entity.getTextureVariant();           // 0..2

        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "textures/entity/spectrobe/" + key + "/" + key + "_color_" + v + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(SpectrobeEntity entity) {
        String key = entity.getSpeciesKey();
        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "animations/spectrobe/" + key + "/" + key + ".animation.json"
        );
    }
}