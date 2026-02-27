// src/main/java/com/jvprz/spectrobesreforged/SpectrobesReforged.java
package com.jvprz.spectrobesreforged;

import com.jvprz.spectrobesreforged.content.prizmod.SpectrobeManager;
import com.jvprz.spectrobesreforged.content.prizmod.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.network.S2CPrizmodSnapshot;
import com.jvprz.spectrobesreforged.registry.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.jvprz.spectrobesreforged.client.ClientEvents;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.jvprz.spectrobesreforged.common.CommonEvents;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

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

        SpectrobeSpeciesRegistry.registerBaby("komainu", SpectrobeManager::spawnKomainu);

        modEventBus.addListener(com.jvprz.spectrobesreforged.network.ModNetwork::register);

        modEventBus.addListener(ClientEvents::registerRenderers);
        modEventBus.addListener(CommonEvents::registerAttributes);
        modEventBus.addListener(ClientEvents::registerScreens);

        LOGGER.info("Spectrobes Reforged loaded");
    }
}