package com.jvprz.spectrobesreforged.client.render;

import com.jvprz.spectrobesreforged.client.model.SpectrobeModel;
import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpectrobeRenderer extends GeoEntityRenderer<SpectrobeEntity> {

    public SpectrobeRenderer(EntityRendererProvider.Context context) {
        super(context, new SpectrobeModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public void render(
            SpectrobeEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        // Si luego el scale depende del stage: entity.getStage() => 0.75/1.0/1.15 etc.
        float scale = 0.75f;
        poseStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }
}