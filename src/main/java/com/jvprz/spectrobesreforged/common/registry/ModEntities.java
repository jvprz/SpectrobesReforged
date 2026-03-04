package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.KomainuEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, SpectrobesReforged.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<KomainuEntity>> KOMAINU =
            ENTITIES.register("komainu",
                    () -> EntityType.Builder.of(KomainuEntity::new, MobCategory.CREATURE)
                            .sized(0.7f, 0.9f)
                            .build("komainu")
            );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}