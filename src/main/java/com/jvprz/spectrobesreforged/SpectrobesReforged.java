// src/main/java/com/jvprz/spectrobesreforged/SpectrobesReforged.java
package com.jvprz.spectrobesreforged;

import org.slf4j.Logger;

import com.jvprz.spectrobesreforged.registry.ModBlocks;
import com.jvprz.spectrobesreforged.registry.ModItems;
import com.jvprz.spectrobesreforged.registry.ModTabs;
import com.jvprz.spectrobesreforged.registry.ModEntities;
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

        // RENDERER (Cliente)
        modEventBus.addListener(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers.class, ClientEvents::registerRenderers);

        // ATRIBUTOS (Común/Servidor) - ESTO ARREGLA EL NULLPOINTEREXCEPTION
        modEventBus.addListener(CommonEvents::registerAttributes);

        LOGGER.info("Spectrobes Reforged loaded");
    }
}