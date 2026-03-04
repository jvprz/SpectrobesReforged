// src/main/java/com/jvprz/spectrobesreforged/registry/ModTabs.java
package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModTabs {
    private ModTabs() {}

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpectrobesReforged.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPECTROBES_BLOCKS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_blocks", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.spectrobes_blocks"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.CORONA_FOSSIL_ORE_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CORONA_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.AURORA_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.FLASH_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.DEEPSLATE_CORONA_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.DEEPSLATE_AURORA_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.DEEPSLATE_FLASH_FOSSIL_ORE_ITEM.get());
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPECTROBES_FOSSILS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_fossils", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.spectrobes_fossils"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.KOMAINU_FOSSIL.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.VILAR_FOSSIL.get());
                        output.accept(ModItems.SEGU_FOSSIL.get());
                        output.accept(ModItems.HARUMI_FOSSIL.get());
                        output.accept(ModItems.SPIKO_FOSSIL.get());
                        output.accept(ModItems.NAGU_FOSSIL.get());
                        output.accept(ModItems.INKANA_FOSSIL.get());
                        output.accept(ModItems.DONGOR_FOSSIL.get());
                        output.accept(ModItems.BARTOR_FOSSIL.get());
                        output.accept(ModItems.AOI_FOSSIL.get());
                        output.accept(ModItems.KOMAINU_FOSSIL.get());
                        output.accept(ModItems.SHAKIN_FOSSIL.get());
                        output.accept(ModItems.ZOZA_FOSSIL.get());
                        output.accept(ModItems.GRILDA_FOSSIL.get());
                        output.accept(ModItems.GEJIO_FOSSIL.get());
                        output.accept(ModItems.KASUMI_FOSSIL.get());
                        output.accept(ModItems.SAMUKABU_FOSSIL.get());
                        output.accept(ModItems.KUBAkU_FOSSIL.get());
                        output.accept(ModItems.MASETTO_FOSSIL.get());
                        output.accept(ModItems.DANAWA_FOSSIL.get());
                        output.accept(ModItems.TENKRO_FOSSIL.get());
                        output.accept(ModItems.MOSSARI_FOSSIL.get());
                        output.accept(ModItems.MESA_FOSSIL.get());
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPECTROBES_MINERALS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_minerals", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.spectrobes_minerals"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.POWER_C.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        for (DeferredItem<Item> mineral : ModItems.ALL_MINERALS) {
                            output.accept(mineral.get());
                        }
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPECTROBES_TOOLS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_tools", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.spectrobes_tools"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.LIGHT_GRAY_PRIZMOD.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.WHITE_PRIZMOD.get());
                        output.accept(ModItems.LIGHT_GRAY_PRIZMOD.get());
                        output.accept(ModItems.GRAY_PRIZMOD.get());
                        output.accept(ModItems.BLACK_PRIZMOD.get());
                        output.accept(ModItems.BROWN_PRIZMOD.get());
                        output.accept(ModItems.RED_PRIZMOD.get());
                        output.accept(ModItems.ORANGE_PRIZMOD.get());
                        output.accept(ModItems.YELLOW_PRIZMOD.get());
                        output.accept(ModItems.LIME_PRIZMOD.get());
                        output.accept(ModItems.GREEN_PRIZMOD.get());
                        output.accept(ModItems.CYAN_PRIZMOD.get());
                        output.accept(ModItems.LIGHT_BLUE_PRIZMOD.get());
                        output.accept(ModItems.BLUE_PRIZMOD.get());
                        output.accept(ModItems.PURPLE_PRIZMOD.get());
                        output.accept(ModItems.MAGENTA_PRIZMOD.get());
                        output.accept(ModItems.PINK_PRIZMOD.get());
                        output.accept(ModItems.INCUBATOR.get());
                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}