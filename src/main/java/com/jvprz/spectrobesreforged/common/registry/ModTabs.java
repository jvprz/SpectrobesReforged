package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.Comparator;
import java.util.List;

public final class ModTabs {
    private ModTabs() {}
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB,SpectrobesReforged.MODID);
    private static ResourceLocation tab(String name){return ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,name);}
    private static CreativeModeTab.Builder category(String name,String previous){
        // Explicit edges preserve the progression order, independent of registry declaration order.
        var builder=CreativeModeTab.builder().title(Component.translatable("itemGroup."+name))
                .withTabsAfter(CreativeModeTabs.OP_BLOCKS);
        return previous==null?builder.withTabsBefore(CreativeModeTabs.SPAWN_EGGS):builder.withTabsBefore(tab(previous));
    }
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> SPECTROBES_TOOLS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_tools",()->category("spectrobes_tools",null)
                    .icon(()->ModItems.WHITE_PRIZMOD.get().getDefaultInstance())
                    .displayItems((parameters,output)->{
                        output.accept(ModItems.WHITE_PRIZMOD.get());
                        output.accept(ModItems.EXCAVATION_TABLE.get());
                        output.accept(ModItems.INCUBATOR.get());
                        ModItems.EXCAVATION_TOOLS.values().forEach(tool->output.accept(tool.get()));
                        // All cosmetic variants stay together after the functional equipment.
                        for(var item:List.of(ModItems.LIGHT_GRAY_PRIZMOD,ModItems.GRAY_PRIZMOD,ModItems.BLACK_PRIZMOD,
                                ModItems.BROWN_PRIZMOD,ModItems.RED_PRIZMOD,ModItems.ORANGE_PRIZMOD,ModItems.YELLOW_PRIZMOD,
                                ModItems.LIME_PRIZMOD,ModItems.GREEN_PRIZMOD,ModItems.CYAN_PRIZMOD,ModItems.LIGHT_BLUE_PRIZMOD,
                                ModItems.BLUE_PRIZMOD,ModItems.PURPLE_PRIZMOD,ModItems.MAGENTA_PRIZMOD,ModItems.PINK_PRIZMOD))output.accept(item.get());
                    }).build());
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> SPECTROBES_FOSSILS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_fossils",()->category("spectrobes_fossils","spectrobes_tools")
                    .icon(()->ModItems.KOMAINU_FOSSIL.get().getDefaultInstance())
                    .displayItems((parameters,output)->{
                        output.accept(ModItems.RAW_CORONA_FOSSIL.get());
                        output.accept(ModItems.RAW_AURORA_FOSSIL.get());
                        output.accept(ModItems.RAW_FLASH_FOSSIL.get());
                        // Every registered fossil is now playable; group by Corona / Aurora / Flash.
                        for(var item:List.of(ModItems.DANAWA_FOSSIL,ModItems.DONGOR_FOSSIL,ModItems.INKANA_FOSSIL,
                                ModItems.KOMAINU_FOSSIL,ModItems.MESA_FOSSIL,ModItems.TENKRO_FOSSIL,ModItems.VILAR_FOSSIL,
                                ModItems.ZOZA_FOSSIL,ModItems.BARTOR_FOSSIL,ModItems.GEJIO_FOSSIL,ModItems.GRILDA_FOSSIL,
                                ModItems.KUBAkU_FOSSIL,ModItems.MASETTO_FOSSIL,ModItems.SEGU_FOSSIL,ModItems.SPIKO_FOSSIL,
                                ModItems.AOI_FOSSIL,ModItems.HARUMI_FOSSIL,ModItems.KASUMI_FOSSIL,ModItems.MOSSARI_FOSSIL,
                                ModItems.NAGU_FOSSIL,ModItems.SAMUKABU_FOSSIL,ModItems.SHAKIN_FOSSIL))output.accept(item.get());
                    }).build());
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> SPECTROBES_MINERALS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_minerals",()->category("spectrobes_minerals","spectrobes_fossils")
                    .icon(()->ModItems.POWER_C.get().getDefaultInstance())
                    .displayItems((parameters,output)->{
                        for(var item:List.of(ModItems.RAW_C_MINERAL,ModItems.RAW_B_MINERAL,ModItems.RAW_A_MINERAL,ModItems.RAW_A_PLUS_MINERAL))output.accept(item.get());
                        // Stat families C -> B -> A -> A+, then combinations, rare minerals and Chroma.
                        for(DeferredItem<Item> item:ModItems.ALL_MINERALS)output.accept(item.get());
                    }).build());
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> SPECTROBES_BLOCKS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_blocks",()->category("spectrobes_blocks","spectrobes_minerals")
                    .icon(()->ModItems.CORONA_FOSSIL_ORE_ITEM.get().getDefaultInstance())
                    .displayItems((parameters,output)->{
                        output.accept(ModItems.CORONA_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.DEEPSLATE_CORONA_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.AURORA_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.DEEPSLATE_AURORA_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.FLASH_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.DEEPSLATE_FLASH_FOSSIL_ORE_ITEM.get());
                        output.accept(ModItems.C_MINERAL_ORE.get());output.accept(ModItems.DEEPSLATE_C_MINERAL_ORE.get());
                        output.accept(ModItems.B_MINERAL_ORE.get());output.accept(ModItems.DEEPSLATE_B_MINERAL_ORE.get());
                        output.accept(ModItems.A_MINERAL_ORE.get());output.accept(ModItems.DEEPSLATE_A_MINERAL_ORE.get());
                        output.accept(ModItems.A_PLUS_MINERAL_ORE.get());output.accept(ModItems.DEEPSLATE_A_PLUS_MINERAL_ORE.get());
                        // Environmental deposits: ice, sand, mud; fossil first, then C/B/A/A+.
                        ModItems.ENVIRONMENTAL_ORES.entrySet().stream().sorted(Comparator
                                .comparingInt((java.util.Map.Entry<String,?> e)->environment(e.getKey()))
                                .thenComparingInt(e->deposit(e.getKey())).thenComparing(java.util.Map.Entry::getKey))
                                .forEach(e->output.accept(e.getValue().get()));
                    }).build());
    private static int environment(String key){return key.startsWith("frozen_")?0:key.startsWith("sandy_")?1:key.startsWith("red_sandy_")?2:key.startsWith("muddy_")?3:4;}
    private static int deposit(String key){return key.endsWith("fossil_ore")?0:key.contains("_c_mineral")?1:key.contains("_b_mineral")?2:key.contains("_a_plus_mineral")?4:3;}
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> SPECTROBES_SPAWN_EGGS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_spawn_eggs",()->category("spectrobes_spawn_eggs","spectrobes_blocks")
                    .icon(()->ModItems.BLOVA_SPAWN_EGG.get().getDefaultInstance())
                    .displayItems((parameters,output)->{
                        output.accept(ModItems.BLOVA_SPAWN_EGG.get());
                        output.accept(ModItems.VIBLOVA_SPAWN_EGG.get());
                        output.accept(ModItems.BLOVANT_SPAWN_EGG.get());
                    }).build());
    public static void register(IEventBus bus){CREATIVE_MODE_TABS.register(bus);}
}
