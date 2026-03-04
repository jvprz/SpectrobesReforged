package com.jvprz.spectrobesreforged.common.event;

import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.registry.ModEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class CommonEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // Esto vincula los atributos que escribiste en KomainuEntity con el registro
        event.put(ModEntities.KOMAINU.get(), SpectrobeEntity.createAttributes().build());
    }
}
