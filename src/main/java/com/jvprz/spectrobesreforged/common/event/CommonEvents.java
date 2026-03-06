package com.jvprz.spectrobesreforged.common.event;

import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.loader.SpectrobeSpeciesLoader;
import com.jvprz.spectrobesreforged.common.registry.ModEntities;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class CommonEvents {

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SPECTROBE.get(), SpectrobeEntity.createAttributes().build());
    }

    public static void registerReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SpectrobeSpeciesLoader());
    }
}