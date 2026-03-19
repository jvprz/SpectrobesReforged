// src/main/java/com/jvprz/spectrobesreforged/registry/ModBlocks.java
package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.block.MineralOreBlock;
import com.jvprz.spectrobesreforged.common.feature.mineral.MineralTier;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeType;
import com.jvprz.spectrobesreforged.common.content.block.FossilOreBlock;

import com.jvprz.spectrobesreforged.common.content.block.IncubatorBlock;
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

    // Fossil ores
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

    // Deepslate fossil ores
    public static final DeferredBlock<Block> DEEPSLATE_CORONA_FOSSIL_ORE =
            BLOCKS.register("deepslate_corona_fossil_ore",
                    () -> new FossilOreBlock(
                            SpectrobeType.CORONA,
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.DEEPSLATE)
                                    .strength(3.0f, 6.0f) // deepslate suele ser más duro
                                    .sound(SoundType.DEEPSLATE)
                                    .requiresCorrectToolForDrops()
                    )
            );

    public static final DeferredBlock<Block> DEEPSLATE_AURORA_FOSSIL_ORE =
            BLOCKS.register("deepslate_aurora_fossil_ore",
                    () -> new FossilOreBlock(
                            SpectrobeType.AURORA,
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.DEEPSLATE)
                                    .strength(3.0f, 6.0f)
                                    .sound(SoundType.DEEPSLATE)
                                    .requiresCorrectToolForDrops()
                    )
            );

    public static final DeferredBlock<Block> DEEPSLATE_FLASH_FOSSIL_ORE =
            BLOCKS.register("deepslate_flash_fossil_ore",
                    () -> new FossilOreBlock(
                            SpectrobeType.FLASH,
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.DEEPSLATE)
                                    .strength(3.0f, 6.0f)
                                    .sound(SoundType.DEEPSLATE)
                                    .requiresCorrectToolForDrops()
                    )
            );

    public static final DeferredBlock<Block> C_MINERAL_ORE =
            BLOCKS.register("c_mineral_ore",
                    () -> new MineralOreBlock(MineralTier.C,
                            BlockBehaviour.Properties.of()
                                    .strength(3.0F, 3.0F)
                                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> B_MINERAL_ORE =
            BLOCKS.register("b_mineral_ore",
                    () -> new MineralOreBlock(MineralTier.B,
                            BlockBehaviour.Properties.of()
                                    .strength(3.0F, 3.0F)
                                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> A_MINERAL_ORE =
            BLOCKS.register("a_mineral_ore",
                    () -> new MineralOreBlock(MineralTier.A,
                            BlockBehaviour.Properties.of()
                                    .strength(3.0F, 3.0F)
                                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> A_PLUS_MINERAL_ORE =
            BLOCKS.register("a_plus_mineral_ore",
                    () -> new MineralOreBlock(MineralTier.A_PLUS,
                            BlockBehaviour.Properties.of()
                                    .strength(3.5F, 4.0F)
                                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DEEPSLATE_C_MINERAL_ORE =
            BLOCKS.register("deepslate_c_mineral_ore",
                    () -> new MineralOreBlock(MineralTier.C,
                            BlockBehaviour.Properties.of()
                                    .strength(3.0F, 3.0F)
                                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DEEPSLATE_B_MINERAL_ORE =
            BLOCKS.register("deepslate_b_mineral_ore",
                    () -> new MineralOreBlock(MineralTier.B,
                            BlockBehaviour.Properties.of()
                                    .strength(3.0F, 3.0F)
                                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DEEPSLATE_A_MINERAL_ORE =
            BLOCKS.register("deepslate_a_mineral_ore",
                    () -> new MineralOreBlock(MineralTier.A,
                            BlockBehaviour.Properties.of()
                                    .strength(3.0F, 3.0F)
                                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DEEPSLATE_A_PLUS_MINERAL_ORE =
            BLOCKS.register("deepslate_a_plus_mineral_ore",
                    () -> new MineralOreBlock(MineralTier.A_PLUS,
                            BlockBehaviour.Properties.of()
                                    .strength(3.5F, 4.0F)
                                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> INCUBATOR =
            BLOCKS.register("incubator",
                    () -> new IncubatorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(2.0F)
                            .sound(SoundType.METAL)
                            .noOcclusion()
                    )
            );

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}