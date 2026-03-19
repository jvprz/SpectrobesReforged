package com.jvprz.spectrobesreforged.client.model;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

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
        String key = entity.getSpeciesKey();
        int v = entity.getTextureVariant();

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

    @Override
    public void setCustomAnimations(SpectrobeEntity animatable, long instanceId, AnimationState<SpectrobeEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        if (animatable.isRangePaused()) {
            return;
        }

        GeoBone head = this.getAnimationProcessor().getBone("head");
        if (head == null) return; // 🔑 modelos sin head → ignorar

        EntityModelData modelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        if (modelData == null) return;

        float yaw = Mth.clamp(modelData.netHeadYaw(), -20.0F, 20.0F);
        float pitch = Mth.clamp(modelData.headPitch(), -10.0F, 15.0F);

        float smooth = 0.25F;

        head.setRotY(head.getRotY() + yaw * Mth.DEG_TO_RAD * smooth);
        head.setRotX(head.getRotX() + pitch * Mth.DEG_TO_RAD * smooth);
    }
}