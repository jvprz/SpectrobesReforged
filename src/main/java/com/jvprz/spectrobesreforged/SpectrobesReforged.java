// src/main/java/com/jvprz/spectrobesreforged/SpectrobesReforged.java
package com.jvprz.spectrobesreforged;

import com.jvprz.spectrobesreforged.client.event.ClientEvents;
import com.jvprz.spectrobesreforged.common.event.CommonEvents;
import com.jvprz.spectrobesreforged.common.registry.*;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(SpectrobesReforged.MODID)
public class SpectrobesReforged {

    public static final String MODID = "spectrobesreforged";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpectrobesReforged(IEventBus modEventBus, ModContainer modContainer) {
        ModEntities.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModTabs.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModMenus.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(ModEvents::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(ModSpectrobeAutoSpawn::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(ModSpectrobeAutoSpawn::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(ModSpectrobeAutoSpawn::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(ModSpectrobePortalGuard::onEntityTravelToDimension);
        NeoForge.EVENT_BUS.addListener(ModSpectrobeFollowGuard::onPlayerTick);

        modEventBus.addListener(CommonEvents::registerAttributes);
        NeoForge.EVENT_BUS.addListener(CommonEvents::registerReloadListeners);

        modEventBus.addListener(com.jvprz.spectrobesreforged.common.network.ModNetwork::register);
        modEventBus.addListener(ClientEvents::registerRenderers);
        modEventBus.addListener(ClientEvents::registerScreens);

        LOGGER.info("Spectrobes Reforged loaded");
    }
}