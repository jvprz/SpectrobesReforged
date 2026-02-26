package com.jvprz.spectrobesreforged.client.render;

import com.jvprz.spectrobesreforged.client.model.KomainuModel;
import com.jvprz.spectrobesreforged.content.entity.KomainuEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KomainuRenderer extends GeoEntityRenderer<KomainuEntity> {

    public KomainuRenderer(EntityRendererProvider.Context context) {
        super(context, new KomainuModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public void render(KomainuEntity entity,
                       float entityYaw,
                       float partialTicks,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight) {

        float scale = 0.75f; // 25% más pequeño
        poseStack.scale(scale, scale, scale);

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }
}