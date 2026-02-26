package com.jvprz.spectrobesreforged.client;

import com.jvprz.spectrobesreforged.client.render.KomainuRenderer;
import com.jvprz.spectrobesreforged.registry.ModEntities;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientEvents {
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Esta forma de escribirlo soluciona el error de "type variable(s) T"
        event.registerEntityRenderer(ModEntities.KOMAINU.get(),
                (EntityRendererProvider.Context context) -> new KomainuRenderer(context));
    }
}
