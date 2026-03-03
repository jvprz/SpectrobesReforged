package com.jvprz.spectrobesreforged.client;

import com.jvprz.spectrobesreforged.client.render.KomainuRenderer;
import com.jvprz.spectrobesreforged.client.screen.PrizmodScreen;
import com.jvprz.spectrobesreforged.registry.ModBlocks;
import com.jvprz.spectrobesreforged.registry.ModEntities;
import com.jvprz.spectrobesreforged.registry.ModMenus;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(
                    ModBlocks.INCUBATOR.get(),
                    RenderType.cutout()
            );
        });
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.KOMAINU.get(),
                context -> new KomainuRenderer(context));
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.PRIZMOD_MENU.get(), PrizmodScreen::new);
    }
}