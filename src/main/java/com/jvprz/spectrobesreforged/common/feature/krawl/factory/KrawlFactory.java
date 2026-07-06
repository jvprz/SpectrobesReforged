package com.jvprz.spectrobesreforged.common.feature.krawl.factory;

import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlEntity;
import com.jvprz.spectrobesreforged.common.feature.krawl.data.KrawlDefinition;
import com.jvprz.spectrobesreforged.common.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;

public final class KrawlFactory {

    private KrawlFactory() {
    }

    public static KrawlEntity create(ServerLevel level, KrawlDefinition definition) {

        KrawlEntity entity = new KrawlEntity(
                ModEntities.KRAWL.get(),
                level
        );

        entity.setDefinition(definition);

        return entity;
    }
}