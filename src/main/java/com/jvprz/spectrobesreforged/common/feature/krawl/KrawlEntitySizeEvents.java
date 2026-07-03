package com.jvprz.spectrobesreforged.common.feature.krawl;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.KrawlEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;

@EventBusSubscriber(modid = SpectrobesReforged.MODID)
public final class KrawlEntitySizeEvents {

    private KrawlEntitySizeEvents() {
    }

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof KrawlEntity krawl)) {
            return;
        }

        KrawlDefinition definition = krawl.getDefinition();

        if (definition == null || definition.entity() == null) {
            SpectrobesReforged.LOGGER.warn("Krawl size event fired, but definition/entity data is null");
            return;
        }

        KrawlEntityData entityData = definition.entity();

        SpectrobesReforged.LOGGER.info(
                "Applying Krawl hitbox: key={}, width={}, height={}",
                definition.key(),
                entityData.width(),
                entityData.height()
        );

        event.setNewSize(
                EntityDimensions.scalable(
                        entityData.width(),
                        entityData.height()
                )
        );
    }
}