package com.jvprz.spectrobesreforged.client.model;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.content.entity.KomainuEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KomainuModel extends GeoModel<KomainuEntity> {

    @Override
    public ResourceLocation getModelResource(KomainuEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, "geo/komainu.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KomainuEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, "textures/entity/komainu.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KomainuEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, "animations/komainu.animation.json");
    }
}
