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
                        // Keep the complete Prizmod palette together, then machines and tools.
                        for(var item:List.of(ModItems.LIGHT_GRAY_PRIZMOD,ModItems.GRAY_PRIZMOD,ModItems.BLACK_PRIZMOD,
                                ModItems.BROWN_PRIZMOD,ModItems.RED_PRIZMOD,ModItems.ORANGE_PRIZMOD,ModItems.YELLOW_PRIZMOD,
                                ModItems.LIME_PRIZMOD,ModItems.GREEN_PRIZMOD,ModItems.CYAN_PRIZMOD,ModItems.LIGHT_BLUE_PRIZMOD,
                                ModItems.BLUE_PRIZMOD,ModItems.PURPLE_PRIZMOD,ModItems.MAGENTA_PRIZMOD,ModItems.PINK_PRIZMOD))output.accept(item.get());
                        output.accept(ModItems.EXCAVATION_TABLE.get());
                        output.accept(ModItems.INCUBATOR.get());
                        ModItems.EXCAVATION_TOOLS.values().forEach(tool->output.accept(tool.get()));
                    }).build());
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> SPECTROBES_FOSSILS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_fossils",()->category("spectrobes_fossils","spectrobes_tools")
                    .icon(()->ModItems.KOMAINU_FOSSIL.get().getDefaultInstance())
                    .displayItems((parameters,output)->{
                        output.accept(ModItems.RAW_CORONA_FOSSIL.get());
                        output.accept(ModItems.RAW_AURORA_FOSSIL.get());
                        output.accept(ModItems.RAW_FLASH_FOSSIL.get());
                        com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationDeposits.all().stream().filter(d->d.naturalFossil())
                                .sorted(Comparator.comparingInt(com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationDeposits.Deposit::hostOrder).thenComparingInt(d->d.contentOrder()))
                                .forEach(d->output.accept(ModItems.ENVIRONMENTAL_RAWS.get(d.rawId()).get()));
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
                    .icon(()->ModMineralLoot.ELEMENTALS.get("flashium_a_plus").get().getDefaultInstance())
                    .displayItems((parameters,output)->{
                        ModItems.RAW_ELEMENTAL_MINERALS.values().forEach(item->output.accept(item.get()));
                        ModMineralLoot.ELEMENTALS.values().forEach(item->output.accept(item.get()));
                        // Stat families C -> B -> A -> A+, then combinations, rare minerals and Chroma.
                        for(DeferredItem<Item> item:ModItems.ALL_MINERALS)output.accept(item.get());
                        ModMineralLoot.SEEDS.values().forEach(item->output.accept(item.get()));
                        ModMineralLoot.EVOLVE.values().forEach(item->output.accept(item.get()));
                    }).build());
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> SPECTROBES_DISCOVERIES_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_discoveries",()->category("spectrobes_discoveries","spectrobes_minerals")
                    .icon(()->ModMineralLoot.LOOT.get("cube_alpha").get().getDefaultInstance())
                    .displayItems((parameters,output)->{
                        ModMineralLoot.MATERIALS.values().forEach(item->output.accept(item.get()));
                        for(String id:List.of("relic_crystal","neo"))output.accept(ModMineralLoot.LOOT.get(id).get());
                        for(String cube:ModMineralLoot.CUBES)output.accept(ModMineralLoot.LOOT.get("cube_"+cube).get());
                        for(String shape:ModMineralLoot.MYSTERIES)output.accept(ModMineralLoot.LOOT.get("mystery_stone_"+shape).get());
                    }).build());
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> SPECTROBES_BLOCKS_TAB =
            CREATIVE_MODE_TABS.register("spectrobes_blocks",()->category("spectrobes_blocks","spectrobes_discoveries")
                    .icon(()->ModItems.CORONA_FOSSIL_ORE_ITEM.get().getDefaultInstance())
                    .displayItems((parameters,output)->{
                        for (String material:List.of("","deepslate_")) {
                            for (String content:List.of("corona_fossil","aurora_fossil","flash_fossil","coronium","aurorium","flashium"))
                                output.accept(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,material+content+"_ore")));
                        }
                        for(String host:List.of("frozen","sandy","red_sandy","muddy")){
                            com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationDeposits.all().stream().filter(d->d.naturalFossil()&&d.host().equals(host))
                                    .sorted(Comparator.comparingInt(d->d.contentOrder())).forEach(d->output.accept(ModItems.ENVIRONMENTAL_ORES.get(d.id()).get()));
                            ModItems.ELEMENTAL_MINERAL_ORES.forEach((id,item)->{if(id.startsWith(host+"_"))output.accept(item.get());});
                        }
                    }).build());
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
