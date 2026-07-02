package com.jvprz.spectrobesreforged.client.render;

import com.jvprz.spectrobesreforged.client.model.KrawlModel;
import com.jvprz.spectrobesreforged.common.content.entity.KrawlEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KrawlRenderer extends GeoEntityRenderer<KrawlEntity> {

    public KrawlRenderer(EntityRendererProvider.Context context) {
        super(context, new KrawlModel());
    }
}