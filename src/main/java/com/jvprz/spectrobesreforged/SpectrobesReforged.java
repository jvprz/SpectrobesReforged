package com.jvprz.spectrobesreforged;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(SpectrobesReforged.MODID)
public class SpectrobesReforged {

    // Must match mod_id in gradle.properties and assets/<modid>/...
    public static final String MODID = "spectrobesreforged";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Deferred Registers (everything registered will use the MODID namespace)
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // ----------------------------
    // Items
    // ----------------------------

    // Placeholder fossil item (can be replaced later with real fossil system)
    public static final DeferredItem<Item> FOSSIL =
            ITEMS.registerSimpleItem("fossil", new Item.Properties());

    // ----------------------------
    // Blocks
    // ----------------------------

    // Fossil Stone block (ore-like)
    public static final DeferredBlock<Block> FOSSIL_STONE =
            BLOCKS.registerSimpleBlock("fossil_stone",
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(1.5f, 6.0f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            );

    // Block item for fossil stone (so you can place it from inventory/creative)
    public static final DeferredItem<BlockItem> FOSSIL_STONE_ITEM =
            ITEMS.registerSimpleBlockItem("fossil_stone", FOSSIL_STONE);

    // ----------------------------
    // Creative Tab (Spectrobes)
    // ----------------------------

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPECTROBES_TAB =
            CREATIVE_MODE_TABS.register("spectrobes", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.spectrobes"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> FOSSIL.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(FOSSIL_STONE_ITEM.get());
                        output.accept(FOSSIL.get());
                    })
                    .build());

    public SpectrobesReforged(IEventBus modEventBus, ModContainer modContainer) {
        // Register Deferred Registers to the mod event bus
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        LOGGER.info("Spectrobes Reforged loaded");
    }
}