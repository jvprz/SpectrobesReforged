package com.jvprz.spectrobesreforged.client.event;

import com.jvprz.spectrobesreforged.client.render.SpectrobeRenderer;
import com.jvprz.spectrobesreforged.client.screen.PrizmodScreen;
import com.jvprz.spectrobesreforged.common.registry.ModBlocks;
import com.jvprz.spectrobesreforged.common.registry.ModEntities;
import com.jvprz.spectrobesreforged.common.registry.ModMenus;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
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
                context -> new SpectrobeRenderer(context));
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.PRIZMOD_MENU.get(), PrizmodScreen::new);
    }
}