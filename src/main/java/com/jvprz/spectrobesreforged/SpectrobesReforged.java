package com.jvprz.spectrobesreforged;

import com.jvprz.spectrobesreforged.client.event.ClientEvents;
import com.jvprz.spectrobesreforged.client.registry.ClientParticleRegistry;
import com.jvprz.spectrobesreforged.common.event.CommonEvents;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.guard.ModSpectrobeFollowGuard;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.guard.ModSpectrobePortalGuard;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.spawn.ModSpectrobeAutoSpawn;
import com.jvprz.spectrobesreforged.common.registry.*;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(SpectrobesReforged.MODID)
public class SpectrobesReforged {

    public static final String MODID =
            "spectrobesreforged";

    public static final Logger LOGGER =
            LogUtils.getLogger();

    public SpectrobesReforged(
            IEventBus modEventBus,
            ModContainer modContainer
    ) {
        // Registries
        ModEntities.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModTabs.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModMenus.register(modEventBus);
        ModSounds.register(modEventBus);
        ModParticles.register(modEventBus);

        // NeoForge events
        NeoForge.EVENT_BUS.addListener(
                ModEvents::onRegisterCommands
        );

        NeoForge.EVENT_BUS.addListener(
                ModSpectrobeAutoSpawn::onPlayerLoggedIn
        );

        NeoForge.EVENT_BUS.addListener(
                ModSpectrobeAutoSpawn::onPlayerRespawn
        );

        NeoForge.EVENT_BUS.addListener(
                ModSpectrobeAutoSpawn::onPlayerChangedDimension
        );

        NeoForge.EVENT_BUS.addListener(
                ModSpectrobePortalGuard::onEntityTravelToDimension
        );

        NeoForge.EVENT_BUS.addListener(
                ModSpectrobeFollowGuard::onPlayerTick
        );

        // Common mod events
        modEventBus.addListener(
                CommonEvents::registerAttributes
        );

        NeoForge.EVENT_BUS.addListener(
                CommonEvents::registerReloadListeners
        );

        modEventBus.addListener(
                com.jvprz.spectrobesreforged.common.network
                        .ModNetwork::register
        );

        // Client mod events
        modEventBus.addListener(
                ClientEvents::onClientSetup
        );

        modEventBus.addListener(
                ClientEvents::registerRenderers
        );

        modEventBus.addListener(
                ClientEvents::registerScreens
        );

        modEventBus.addListener(
                ClientParticleRegistry::registerParticleProviders
        );

        LOGGER.info(
                "Spectrobes Reforged loaded"
        );
    }
}