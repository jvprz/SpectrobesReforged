package com.jvprz.spectrobesreforged.client.event;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.config.ClientConfig;
import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.training.MedalSprites;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;

@EventBusSubscriber(modid=SpectrobesReforged.MODID,value=Dist.CLIENT)
public final class CompanionNameMedals {
    private CompanionNameMedals() {}
    @SubscribeEvent public static void name(RenderNameTagEvent event) {
        if(event.getEntity() instanceof SpectrobeEntity && !ClientConfig.nameMedals())event.setContent(MedalSprites.withoutMedals(event.getContent()));
    }
}
