package com.jvprz.spectrobesreforged.client.render;

import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlVortexEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** A continuous tapered funnel with flowing spiral ribbons, rather than loose dust particles. */
public final class KrawlVortexRenderer extends EntityRenderer<KrawlVortexEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/white_concrete.png");
    private static final int SIDES = 40;
    private static final int LAYERS = 22;
    private static final double HEIGHT = 2.65;

    public KrawlVortexRenderer(EntityRendererProvider.Context context) { super(context); }

    @Override
    public void render(KrawlVortexEntity entity, float yaw, float partialTick, PoseStack poses,
                       MultiBufferSource buffers, int packedLight) {
        float dissolve = entity.dissolveProgress(partialTick);
        if (dissolve >= 1) return;
        poses.pushPose();
        float scale = 1 - dissolve;
        poses.translate(0, dissolve * 0.6, 0);
        poses.scale(scale, scale, scale);
        var consumer = buffers.getBuffer(RenderType.entityTranslucent(TEXTURE));
        double time = (entity.tickCount + partialTick) * 0.075;
        float alpha = 1 - dissolve;

        for (int layer = 0; layer < LAYERS; layer++) {
            double lower = layer / (double) LAYERS;
            double upper = (layer + 1) / (double) LAYERS;
            for (int side = 0; side < SIDES; side++) {
                double a = side * Math.PI * 2 / SIDES;
                double b = (side + 1) * Math.PI * 2 / SIDES;
                double ripple = Math.sin(a * 3 + lower * 12 - time * 2) * 0.5 + 0.5;
                int red = (int) (20 + ripple * 15);
                int green = (int) (13 + ripple * 6);
                int blue = (int) (34 + ripple * 24);
                int opacity = (int) (alpha * (175 - lower * 65));
                point(consumer, poses, lower, a, time, 1, red, green, blue, opacity, packedLight);
                point(consumer, poses, upper, a, time, 1, red, green, blue, opacity, packedLight);
                point(consumer, poses, upper, b, time, 1, red, green, blue, opacity, packedLight);
                point(consumer, poses, lower, b, time, 1, red, green, blue, opacity, packedLight);
            }
        }
        // Three broad violet ribbons follow the funnel and animate around its body.
        for (int ribbon = 0; ribbon < 3; ribbon++) {
            for (int step = 0; step < 72; step++) {
                double t0 = step / 72.0;
                double t1 = (step + 1) / 72.0;
                double a0 = ribbon * Math.PI * 2 / 3 + t0 * Math.PI * 4 - time * 2;
                double a1 = ribbon * Math.PI * 2 / 3 + t1 * Math.PI * 4 - time * 2;
                int opacity = (int) (alpha * Math.sin(t0 * Math.PI) * 175);
                point(consumer, poses, t0, a0 - 0.12, time, 1.015, 100, 40, 145, opacity, packedLight);
                point(consumer, poses, t1, a1 - 0.12, time, 1.015, 100, 40, 145, opacity, packedLight);
                point(consumer, poses, t1, a1 + 0.12, time, 1.015, 130, 63, 184, opacity, packedLight);
                point(consumer, poses, t0, a0 + 0.12, time, 1.015, 130, 63, 184, opacity, packedLight);
            }
        }
        poses.popPose();
        super.render(entity, yaw, partialTick, poses, buffers, packedLight);
    }

    private static void point(VertexConsumer consumer, PoseStack poses, double height, double angle,
                              double time, double shell, int r, int g, int b, int alpha, int light) {
        double radius = (0.12 + Math.pow(height, 0.72) * 0.96) * shell;
        radius *= 1 + Math.sin(angle * 3 + height * 12 - time * 2) * 0.045;
        double driftX = Math.sin(time + height * 3) * height * 0.07;
        double driftZ = Math.cos(time * 0.8 + height * 3) * height * 0.07;
        consumer.addVertex(poses.last().pose(), (float) (Math.cos(angle) * radius + driftX),
                        (float) (height * HEIGHT), (float) (Math.sin(angle) * radius + driftZ))
                .setColor(r, g, b, alpha).setUv((float) ((angle / (Math.PI * 2)) % 1), (float) height)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                .setNormal(poses.last(), (float) Math.cos(angle), -0.3F, (float) Math.sin(angle));
    }

    @Override public ResourceLocation getTextureLocation(KrawlVortexEntity entity) { return TEXTURE; }
}
