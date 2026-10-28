package com.jvprz.spectrobesreforged.client.init;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.client.ui.tooltip.TypeTooltipRenderer;
import com.jvprz.spectrobesreforged.common.ui.tooltip.TypeTooltipComponent;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = SpectrobesReforged.MODID, dist = Dist.CLIENT)
public class SpectrobesReforgedClient {

    public SpectrobesReforgedClient(ModContainer container) {

        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT,
                com.jvprz.spectrobesreforged.common.config.ClientConfig.SPEC);
        var bus = container.getEventBus();
        bus.addListener(com.jvprz.spectrobesreforged.client.event.ClientEvents::onClientSetup);
        bus.addListener(com.jvprz.spectrobesreforged.client.event.ClientEvents::registerRenderers);
        bus.addListener(com.jvprz.spectrobesreforged.client.event.ClientEvents::registerScreens);
        bus.addListener(com.jvprz.spectrobesreforged.client.registry.ClientParticleRegistry::registerParticleProviders);

        // Config screen
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Registrar eventos client
        container.getEventBus().addListener(this::registerTooltips);
    }

    private void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(TypeTooltipComponent.class, TypeTooltipRenderer::new);
    }
}