package com.jvprz.spectrobesreforged.client.render;

import com.jvprz.spectrobesreforged.client.model.KrawlVortexModel;
import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlVortexEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class KrawlVortexRenderer extends GeoEntityRenderer<KrawlVortexEntity> {
    public KrawlVortexRenderer(EntityRendererProvider.Context context) {
        super(context, new KrawlVortexModel());
        shadowRadius = 0;
    }

    @Override
    public void render(KrawlVortexEntity entity, float yaw, float partialTick, PoseStack poses,
                       MultiBufferSource buffers, int packedLight) {
        // The entity remains as an invisible encounter controller after the catch animation.
        if (entity.dissolveProgress(partialTick) >= 1) return;
        poses.pushPose();
        poses.scale(1.55F, 1.55F, 1.55F);
        super.render(entity, 0, partialTick, poses, buffers, packedLight);
        poses.popPose();
    }
}
