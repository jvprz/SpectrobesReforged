package com.jvprz.spectrobesreforged.client.model;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SpectrobeModel extends GeoModel<SpectrobeEntity> {
    private GeoBone carryingMouth;
    private float originalMouthRotation;
    private GeoBone correctedHead;
    private AnimatedHeadLook.Pose originalHead;

    /** Custom look offsets must never become the next frame's animation input. */
    public void restoreAnimatedHead() {
        if (carryingMouth != null) {
            carryingMouth.setRotX(originalMouthRotation);
            carryingMouth = null;
        }
        if (correctedHead == null) return;
        var bone=correctedHead;
        var pose=originalHead;
        bone.setPosX(pose.x());bone.setPosY(pose.y());bone.setPosZ(pose.z());
        bone.setRotX(pose.rx());bone.setRotY(pose.ry());bone.setRotZ(pose.rz());
        correctedHead=null;
        originalHead=null;
    }


    @Override
    public ResourceLocation getModelResource(SpectrobeEntity entity) {
        String key = entity.getSpeciesKey();
        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "geo/entity/spectrobe/" + key + ".geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(SpectrobeEntity entity) {
        String key = entity.getSpeciesKey();
        int v = entity.getTextureVariant();

        ResourceLocation texture=ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "textures/entity/spectrobe/" + key + "/" + key + "_color_" + v + ".png"
        );
        // Some bundled species only have their base color painted so far.
        if(v>0&&net.minecraft.client.Minecraft.getInstance().getResourceManager().getResource(texture).isEmpty()) {
            return ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,
                    "textures/entity/spectrobe/"+key+"/"+key+"_color_0.png");
        }
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SpectrobeEntity entity) {
        String key = entity.getSpeciesKey();
        return ResourceLocation.fromNamespaceAndPath(
                SpectrobesReforged.MODID,
                "animations/entity/spectrobe/" + key + ".animation.json"
        );
    }

    @Override
    public void setCustomAnimations(SpectrobeEntity animatable, long instanceId, AnimationState<SpectrobeEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        // Hold half of Spiko's 22.5-degree cry opening while carrying a mineral.
        // Apply after animation sampling, then restore after rendering to avoid pose accumulation.
        if ("spiko".equals(animatable.getSpeciesKey()) && animatable.hasCarriedMineral()) {
            GeoBone mouth = getAnimationProcessor().getBone("mouth");
            if (mouth != null) {
                carryingMouth = mouth;
                originalMouthRotation = mouth.getRotX();
                mouth.setRotX(Math.min(originalMouthRotation, -11.25F * Mth.DEG_TO_RAD));
            }
        }

        if (animatable.isRangePaused() || animatable.getLifeGesture()==1 || (animatable.hasDetailedCompanionRig()&&animatable.getLifeGesture()>=2) || animatable.getReaction()!=0 || animatable.isPreviewAnimationFrozen()) {
            return;
        }

        GeoBone head = this.getAnimationProcessor().getBone("head");
        if (head == null) return;

        EntityModelData modelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        if (modelData == null) return;

        boolean life=com.jvprz.spectrobesreforged.common.config.Config.livingCompanions();
        float weight=life ? .65F : .25F;
        float yaw=Mth.clamp(modelData.netHeadYaw(),-24,24)*Mth.DEG_TO_RAD*weight;
        float pitch=Mth.clamp(modelData.headPitch(),-12,12)*Mth.DEG_TO_RAD*weight;
        float roll=0;
        if (life && !animationState.isMoving() && !com.jvprz.spectrobesreforged.common.config.ClientConfig.reducedMotion()) {
            double time=animationState.getAnimationTick()+animatable.getId()*13.7;
            roll=(float)Math.sin(time*.045)*.012F;
            if(animatable.getLifeGesture()==2)roll+=.035F;
            pitch+=(float)Math.sin(time*.08)*.008F;
        }
        // Keep every animated rotation and translation; never restore the bind pose here.
        var animated=new AnimatedHeadLook.Pose(head.getPosX(),head.getPosY(),head.getPosZ(),head.getRotX(),head.getRotY(),head.getRotZ());
        // Legacy rigs pivot at the origin rather than the neck. Compensate in model space,
        // without modifying the artist's geometry, pivots, keyframes or child bones.
        org.joml.Vector3f anchor=switch(animatable.getSpeciesKey()) {
            case "komainu" -> new org.joml.Vector3f(0,10,-3);
            case "spiko" -> new org.joml.Vector3f(0,5,-4);
            case "vilar" -> new org.joml.Vector3f(0,15,-7);
            default -> new org.joml.Vector3f(head.getPivotX(),head.getPivotY(),head.getPivotZ());
        };
        anchor.sub(head.getPivotX(),head.getPivotY(),head.getPivotZ());
        if (animatable.hasDetailedCompanionRig()) {
            correctedHead=head;
            originalHead=animated;
        }
        var pose=AnimatedHeadLook.compose(animated,anchor,new org.joml.Vector3f(head.getScaleX(),head.getScaleY(),head.getScaleZ()),yaw,pitch,roll);
        head.setRotX(pose.rx());head.setRotY(pose.ry());head.setRotZ(pose.rz());
        head.setPosX(pose.x());head.setPosY(pose.y());head.setPosZ(pose.z());
    }
}
