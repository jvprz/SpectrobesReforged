package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.block.entity.IncubatorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {

    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    SpectrobesReforged.MODID
            );

    public static final DeferredHolder<
            BlockEntityType<?>,
            BlockEntityType<IncubatorBlockEntity>
            > INCUBATOR =
            BLOCK_ENTITIES.register(
                    "incubator",
                    () -> BlockEntityType.Builder.of(
                            IncubatorBlockEntity::new,
                            ModBlocks.INCUBATOR.get()
                    ).build(null)
            );

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}