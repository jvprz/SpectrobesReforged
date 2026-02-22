// src/main/java/com/jvprz/spectrobesreforged/registry/ModBlocks.java
package com.jvprz.spectrobesreforged.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.content.SpectrobeType;
import com.jvprz.spectrobesreforged.content.block.FossilOreBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(SpectrobesReforged.MODID);

    // Fossil ores (same look, different drops)
    public static final DeferredBlock<Block> CORONA_FOSSIL_ORE =
            BLOCKS.register("corona_fossil_ore",
                    () -> new FossilOreBlock(
                            SpectrobeType.CORONA,
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.STONE)
                                    .strength(1.5f, 6.0f)
                                    .sound(SoundType.STONE)
                                    .requiresCorrectToolForDrops()
                    )
            );

    public static final DeferredBlock<Block> AURORA_FOSSIL_ORE =
            BLOCKS.register("aurora_fossil_ore",
                    () -> new FossilOreBlock(
                            SpectrobeType.AURORA,
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.STONE)
                                    .strength(1.5f, 6.0f)
                                    .sound(SoundType.STONE)
                                    .requiresCorrectToolForDrops()
                    )
            );

    public static final DeferredBlock<Block> FLASH_FOSSIL_ORE =
            BLOCKS.register("flash_fossil_ore",
                    () -> new FossilOreBlock(
                            SpectrobeType.FLASH,
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.STONE)
                                    .strength(1.5f, 6.0f)
                                    .sound(SoundType.STONE)
                                    .requiresCorrectToolForDrops()
                    )
            );

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}