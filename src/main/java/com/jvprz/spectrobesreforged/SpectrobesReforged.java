// src/main/java/com/jvprz/spectrobesreforged/SpectrobesReforged.java
package com.jvprz.spectrobesreforged;

import org.slf4j.Logger;

import com.jvprz.spectrobesreforged.registry.ModBlocks;
import com.jvprz.spectrobesreforged.registry.ModItems;
import com.jvprz.spectrobesreforged.registry.ModTabs;
import com.jvprz.spectrobesreforged.client.SpectrobesClient;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(SpectrobesReforged.MODID)
public class SpectrobesReforged {

    public static final String MODID = "spectrobesreforged";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpectrobesReforged(IEventBus modEventBus, ModContainer modContainer) {

        // Registers
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModTabs.register(modEventBus);

        modEventBus.addListener(SpectrobesClient::registerTooltipFactories);

        LOGGER.info("Spectrobes Reforged loaded");
    }
}