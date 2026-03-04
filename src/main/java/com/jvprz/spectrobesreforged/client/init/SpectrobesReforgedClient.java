package com.jvprz.spectrobesreforged.client.init;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.client.ui.tooltip.TypeTooltipRenderer;
import com.jvprz.spectrobesreforged.common.ui.tooltip.TypeTooltipComponent;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = SpectrobesReforged.MODID, dist = Dist.CLIENT)
public class SpectrobesReforgedClient {

    public SpectrobesReforgedClient(ModContainer container) {

        // Config screen
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Registrar eventos client
        container.getEventBus().addListener(this::onClientSetup);
        container.getEventBus().addListener(this::registerTooltips);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        SpectrobesReforged.LOGGER.info("HELLO FROM CLIENT SETUP");
        SpectrobesReforged.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    private void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(TypeTooltipComponent.class, TypeTooltipRenderer::new);
    }
}