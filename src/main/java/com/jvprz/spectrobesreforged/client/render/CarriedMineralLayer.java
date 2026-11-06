package com.jvprz.spectrobesreforged.client.render;

import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/** Render inside the already animated bone matrix; never apply its rotation a second time. */
public final class CarriedMineralLayer extends GeoRenderLayer<SpectrobeEntity> {
    public CarriedMineralLayer(GeoRenderer<SpectrobeEntity> renderer) {super(renderer);}
    private static String boneFor(String species) {
        return switch(species) {case "komainu","spiko"->"item";case "vilar","zoza"->"head";default->"body";};
    }
    @Override public void renderForBone(PoseStack pose,SpectrobeEntity mob,GeoBone bone,RenderType renderType,
            MultiBufferSource buffers,VertexConsumer buffer,float partialTick,int light,int overlay) {
        if(!mob.hasCarriedMineral() || !bone.getName().equals(boneFor(mob.getSpeciesKey())))return;
        Vector3f mouth=switch(mob.getSpeciesKey()) {
            case "komainu","spiko" -> new Vector3f(bone.getPivotX(),bone.getPivotY(),bone.getPivotZ());
            case "vilar" -> new Vector3f(0,16,-10.2F);
            case "zoza" -> new Vector3f(0,4.5F,-7.6F);
            case "segu" -> new Vector3f(0,6,-5.4F);
            case "inkana" -> new Vector3f(0,15,-5.8F);
            case "shakin" -> new Vector3f(6,9.5F,-1.5F);
            default -> new Vector3f(0,6,-4);
        };
        if ("spiko".equals(mob.getSpeciesKey())) mouth.y += 1;
        pose.pushPose();
        pose.translate(mouth.x/16f,mouth.y/16f,mouth.z/16f);
        if (mob.hasDetailedCompanionRig()) {
            pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
        }
        pose.scale(.32F,.32F,.32F);
        Minecraft.getInstance().getItemRenderer().renderStatic(mob,mob.getMainHandItem(),ItemDisplayContext.FIXED,false,
                pose,buffers,mob.level(),light,overlay,mob.getId());
        pose.popPose();buffers.getBuffer(renderType);
    }
}
