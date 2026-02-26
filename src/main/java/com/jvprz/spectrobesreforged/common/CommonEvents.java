package com.jvprz.spectrobesreforged.common;

import com.jvprz.spectrobesreforged.content.entity.KomainuEntity;
import com.jvprz.spectrobesreforged.registry.ModEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class CommonEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // Esto vincula los atributos que escribiste en KomainuEntity con el registro
        event.put(ModEntities.KOMAINU.get(), KomainuEntity.createAttributes().build());
    }
}
