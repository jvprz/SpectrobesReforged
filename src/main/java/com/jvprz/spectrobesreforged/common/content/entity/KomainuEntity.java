package com.jvprz.spectrobesreforged.common.content.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class KomainuEntity extends SpectrobeEntity {

    public KomainuEntity(EntityType<? extends net.minecraft.world.entity.PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected String getDefaultSpeciesKey() {
        return "komainu";
    }
}