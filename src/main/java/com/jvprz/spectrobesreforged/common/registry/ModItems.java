// src/main/java/com/jvprz/spectrobesreforged/registry/ModItems.java
package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.item.*;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeType;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.Rarity;

import java.util.List;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SpectrobesReforged.MODID);

    /** Separate components ready for future table recipes; the table still supplies its tools. */
    public static final java.util.Map<String,DeferredItem<Item>> EXCAVATION_TOOLS = excavationTools();
    private static java.util.Map<String,DeferredItem<Item>> excavationTools() {
        var result=new java.util.LinkedHashMap<String,DeferredItem<Item>>();
        for(String tool:List.of("drill","blower","recovery","piton","fire","water","blower_s","vacuum","giga","blast")) {
            result.put(tool,ITEMS.register("excavation_"+tool,()->tool.equals("blower")?new BlowerItem():new Item(new Item.Properties().stacksTo(1))));
        }
        return java.util.Collections.unmodifiableMap(result);
    }

    public static final DeferredItem<BlockItem> EXCAVATION_TABLE = ITEMS.registerSimpleBlockItem("excavation_table", ModBlocks.EXCAVATION_TABLE);
    public static final DeferredItem<Item> RAW_CORONA_FOSSIL = ITEMS.register("raw_corona_fossil", () -> new RawFossilItem(SpectrobeType.CORONA, new Item.Properties()));
    public static final DeferredItem<Item> RAW_AURORA_FOSSIL = ITEMS.register("raw_aurora_fossil", () -> new RawFossilItem(SpectrobeType.AURORA, new Item.Properties()));
    public static final DeferredItem<Item> RAW_FLASH_FOSSIL = ITEMS.register("raw_flash_fossil", () -> new RawFossilItem(SpectrobeType.FLASH, new Item.Properties()));

    public static final DeferredItem<Item> RAW_C_MINERAL = ITEMS.register("raw_c_mineral", () -> new RawMineralItem("c"));
    public static final DeferredItem<Item> RAW_B_MINERAL = ITEMS.register("raw_b_mineral", () -> new RawMineralItem("b"));
    public static final DeferredItem<Item> RAW_A_MINERAL = ITEMS.register("raw_a_mineral", () -> new RawMineralItem("a"));
    public static final DeferredItem<Item> RAW_A_PLUS_MINERAL = ITEMS.register("raw_a_plus_mineral", () -> new RawMineralItem("a_plus"));

    // CORONA
    public static final DeferredItem<Item> VILAR_FOSSIL =
            ITEMS.register("vilar_fossil", () -> new FossilItem(SpectrobeType.CORONA, new Item.Properties()));

    public static final DeferredItem<Item> INKANA_FOSSIL =
            ITEMS.register("inkana_fossil", () -> new FossilItem(SpectrobeType.CORONA, new Item.Properties()));

    public static final DeferredItem<Item> DONGOR_FOSSIL =
            ITEMS.register("dongor_fossil", () -> new FossilItem(SpectrobeType.CORONA, new Item.Properties()));

    public static final DeferredItem<Item> KOMAINU_FOSSIL =
            ITEMS.register("komainu_fossil", () -> new FossilItem(SpectrobeType.CORONA, new Item.Properties()));

    public static final DeferredItem<Item> ZOZA_FOSSIL =
            ITEMS.register("zoza_fossil", () -> new FossilItem(SpectrobeType.CORONA, new Item.Properties()));

    public static final DeferredItem<Item> DANAWA_FOSSIL =
            ITEMS.register("danawa_fossil", () -> new FossilItem(SpectrobeType.CORONA, new Item.Properties()));

    public static final DeferredItem<Item> TENKRO_FOSSIL =
            ITEMS.register("tenkro_fossil", () -> new FossilItem(SpectrobeType.CORONA, new Item.Properties()));

    public static final DeferredItem<Item> MESA_FOSSIL =
            ITEMS.register("mesa_fossil", () -> new FossilItem(SpectrobeType.CORONA, new Item.Properties()));


    // AURORA
    public static final DeferredItem<Item> SEGU_FOSSIL =
            ITEMS.register("segu_fossil", () -> new FossilItem(SpectrobeType.AURORA, new Item.Properties()));

    public static final DeferredItem<Item> SPIKO_FOSSIL =
            ITEMS.register("spiko_fossil", () -> new FossilItem(SpectrobeType.AURORA, new Item.Properties()));

    public static final DeferredItem<Item> BARTOR_FOSSIL =
            ITEMS.register("bartor_fossil", () -> new FossilItem(SpectrobeType.AURORA, new Item.Properties()));

    public static final DeferredItem<Item> GRILDA_FOSSIL =
            ITEMS.register("grilda_fossil", () -> new FossilItem(SpectrobeType.AURORA, new Item.Properties()));

    public static final DeferredItem<Item> GEJIO_FOSSIL =
            ITEMS.register("gejio_fossil", () -> new FossilItem(SpectrobeType.AURORA, new Item.Properties()));

    public static final DeferredItem<Item> KUBAkU_FOSSIL =
            ITEMS.register("kubaku_fossil", () -> new FossilItem(SpectrobeType.AURORA, new Item.Properties()));

    public static final DeferredItem<Item> MASETTO_FOSSIL =
            ITEMS.register("masetto_fossil", () -> new FossilItem(SpectrobeType.AURORA, new Item.Properties()));


    // FLASH
    public static final DeferredItem<Item> HARUMI_FOSSIL =
            ITEMS.register("harumi_fossil", () -> new FossilItem(SpectrobeType.FLASH, new Item.Properties()));

    public static final DeferredItem<Item> NAGU_FOSSIL =
            ITEMS.register("nagu_fossil", () -> new FossilItem(SpectrobeType.FLASH, new Item.Properties()));

    public static final DeferredItem<Item> AOI_FOSSIL =
            ITEMS.register("aoi_fossil", () -> new FossilItem(SpectrobeType.FLASH, new Item.Properties()));

    public static final DeferredItem<Item> SHAKIN_FOSSIL =
            ITEMS.register("shakin_fossil", () -> new FossilItem(SpectrobeType.FLASH, new Item.Properties()));

    public static final DeferredItem<Item> KASUMI_FOSSIL =
            ITEMS.register("kasumi_fossil", () -> new FossilItem(SpectrobeType.FLASH, new Item.Properties()));

    public static final DeferredItem<Item> SAMUKABU_FOSSIL =
            ITEMS.register("samukabu_fossil", () -> new FossilItem(SpectrobeType.FLASH, new Item.Properties()));

    public static final DeferredItem<Item> MOSSARI_FOSSIL =
            ITEMS.register("mossari_fossil", () -> new FossilItem(SpectrobeType.FLASH, new Item.Properties()));


    // Block items
    public static final DeferredItem<BlockItem> CORONA_FOSSIL_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("corona_fossil_ore", ModBlocks.CORONA_FOSSIL_ORE);

    public static final DeferredItem<BlockItem> AURORA_FOSSIL_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("aurora_fossil_ore", ModBlocks.AURORA_FOSSIL_ORE);

    public static final DeferredItem<BlockItem> FLASH_FOSSIL_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("flash_fossil_ore", ModBlocks.FLASH_FOSSIL_ORE);

    public static final DeferredItem<BlockItem> DEEPSLATE_CORONA_FOSSIL_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("deepslate_corona_fossil_ore", ModBlocks.DEEPSLATE_CORONA_FOSSIL_ORE);

    public static final DeferredItem<BlockItem> DEEPSLATE_AURORA_FOSSIL_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("deepslate_aurora_fossil_ore", ModBlocks.DEEPSLATE_AURORA_FOSSIL_ORE);

    public static final DeferredItem<BlockItem> DEEPSLATE_FLASH_FOSSIL_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("deepslate_flash_fossil_ore", ModBlocks.DEEPSLATE_FLASH_FOSSIL_ORE);

    public static final DeferredItem<Item> C_MINERAL_ORE =
            ITEMS.register("c_mineral_ore",
                    () -> new BlockItem(ModBlocks.C_MINERAL_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> B_MINERAL_ORE =
            ITEMS.register("b_mineral_ore",
                    () -> new BlockItem(ModBlocks.B_MINERAL_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> A_MINERAL_ORE =
            ITEMS.register("a_mineral_ore",
                    () -> new BlockItem(ModBlocks.A_MINERAL_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> A_PLUS_MINERAL_ORE =
            ITEMS.register("a_plus_mineral_ore",
                    () -> new BlockItem(ModBlocks.A_PLUS_MINERAL_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> DEEPSLATE_C_MINERAL_ORE =
            ITEMS.register("deepslate_c_mineral_ore",
                    () -> new BlockItem(ModBlocks.DEEPSLATE_C_MINERAL_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> DEEPSLATE_B_MINERAL_ORE =
            ITEMS.register("deepslate_b_mineral_ore",
                    () -> new BlockItem(ModBlocks.DEEPSLATE_B_MINERAL_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> DEEPSLATE_A_MINERAL_ORE =
            ITEMS.register("deepslate_a_mineral_ore",
                    () -> new BlockItem(ModBlocks.DEEPSLATE_A_MINERAL_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> DEEPSLATE_A_PLUS_MINERAL_ORE =
            ITEMS.register("deepslate_a_plus_mineral_ore",
                    () -> new BlockItem(ModBlocks.DEEPSLATE_A_PLUS_MINERAL_ORE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> INCUBATOR =
            ITEMS.registerSimpleBlockItem("incubator", ModBlocks.INCUBATOR);

    // Minerals
    private static final boolean MAX = true;
    private static final boolean VAL = false;

    // Minerals common tier
    public static final DeferredItem<Item> POWER_C =
            ITEMS.register("power_c",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            0, VAL, 8, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.power_c"));

    public static final DeferredItem<Item> POWER_B =
            ITEMS.register("power_b",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            0, VAL, 16, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.power_b"));

    public static final DeferredItem<Item> DEFENSE_C =
            ITEMS.register("defense_c",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            0, VAL, 0, VAL, 8, VAL,
                            "tooltip.spectrobesreforged.mineral.defense_c"));

    public static final DeferredItem<Item> DEFENSE_B =
            ITEMS.register("defense_b",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            0, VAL, 0, VAL, 16, VAL,
                            "tooltip.spectrobesreforged.mineral.defense_b"));

    public static final DeferredItem<Item> HEALTH_C =
            ITEMS.register("health_c",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            8, VAL, 0, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.health_c"));

    public static final DeferredItem<Item> HEALTH_B =
            ITEMS.register("health_b",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            16, VAL, 0, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.health_b"));

    // Minerals tier uncommon
    public static final DeferredItem<Item> POWER_A =
            ITEMS.register("power_a",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 24, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.power_a"));

    public static final DeferredItem<Item> POWER_A_PLUS =
            ITEMS.register("power_a_plus",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 32, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.power_a_plus"));

    public static final DeferredItem<Item> DEFENSE_A =
            ITEMS.register("defense_a",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 0, VAL, 24, VAL,
                            "tooltip.spectrobesreforged.mineral.defense_a"));

    public static final DeferredItem<Item> DEFENSE_A_PLUS =
            ITEMS.register("defense_a_plus",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 0, VAL, 32, VAL,
                            "tooltip.spectrobesreforged.mineral.defense_a_plus"));

    public static final DeferredItem<Item> HEALTH_A =
            ITEMS.register("health_a",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            24, VAL, 0, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.health_a"));

    public static final DeferredItem<Item> HEALTH_A_PLUS =
            ITEMS.register("health_a_plus",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            32, VAL, 0, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.health_a_plus"));

    public static final DeferredItem<Item> AGATE =
            ITEMS.register("agate",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 8, VAL, 8, VAL,
                            "tooltip.spectrobesreforged.mineral.agate"));

    public static final DeferredItem<Item> LAZULI =
            ITEMS.register("lazuli",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 16, VAL, 16, VAL,
                            "tooltip.spectrobesreforged.mineral.lazuli"));

    public static final DeferredItem<Item> JADE =
            ITEMS.register("jade",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            8, VAL, 8, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.jade"));

    public static final DeferredItem<Item> AMBER =
            ITEMS.register("amber",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            16, VAL, 16, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.amber"));

    public static final DeferredItem<Item> FLUORITE =
            ITEMS.register("fluorite",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            8, VAL, 0, VAL, 8, VAL,
                            "tooltip.spectrobesreforged.mineral.fluorite"));

    public static final DeferredItem<Item> CORAL =
            ITEMS.register("coral",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            16, VAL, 0, VAL, 16, VAL,
                            "tooltip.spectrobesreforged.mineral.coral"));

    // Minerals tier rare
    public static final DeferredItem<Item> GRAPHITE =
            ITEMS.register("graphite",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            0, VAL, 30, VAL, -8, VAL,
                            "tooltip.spectrobesreforged.mineral.graphite"));

    public static final DeferredItem<Item> GARNET =
            ITEMS.register("garnet",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            0, VAL, -8, VAL, 30, VAL,
                            "tooltip.spectrobesreforged.mineral.garnet"));

    public static final DeferredItem<Item> ONYX =
            ITEMS.register("onyx",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            0, VAL, 40, VAL, -16, VAL,
                            "tooltip.spectrobesreforged.mineral.onyx"));

    public static final DeferredItem<Item> TOPAZ =
            ITEMS.register("topaz",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            0, VAL, -16, VAL, 40, VAL,
                            "tooltip.spectrobesreforged.mineral.topaz"));

    public static final DeferredItem<Item> ZIRCON =
            ITEMS.register("zircon",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            -8, VAL, 30, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.zircon"));

    public static final DeferredItem<Item> OPAL =
            ITEMS.register("opal",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            30, VAL, -8, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.opal"));

    public static final DeferredItem<Item> COBALT =
            ITEMS.register("cobalt",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            -16, VAL, 40, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.cobalt"));

    public static final DeferredItem<Item> CITRINE =
            ITEMS.register("citrine",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            40, VAL, -16, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.citrine"));

    public static final DeferredItem<Item> SYLVITE =
            ITEMS.register("sylvite",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            -8, VAL, 0, VAL, 30, VAL,
                            "tooltip.spectrobesreforged.mineral.sylvite"));

    public static final DeferredItem<Item> SPINAL =
            ITEMS.register("spinal",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            30, VAL, 0, VAL, -8, VAL,
                            "tooltip.spectrobesreforged.mineral.spinal"));

    public static final DeferredItem<Item> AZURITE =
            ITEMS.register("azurite",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            -16, VAL, 0, VAL, 40, VAL,
                            "tooltip.spectrobesreforged.mineral.azurite"));

    public static final DeferredItem<Item> QUARTZ =
            ITEMS.register("quartz",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            40, VAL, 0, VAL, -16, VAL,
                            "tooltip.spectrobesreforged.mineral.quartz"));

    public static final DeferredItem<Item> PLATINUM =
            ITEMS.register("platinum",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            5, VAL, 5, VAL, 5, VAL,
                            "tooltip.spectrobesreforged.mineral.platinum"));

    public static final DeferredItem<Item> PEARL =
            ITEMS.register("pearl",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            12, VAL, 12, VAL, 12, VAL,
                            "tooltip.spectrobesreforged.mineral.pearl"));

    // Minerals tier epic
    public static final DeferredItem<Item> URANIUM =
            ITEMS.register("uranium",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            -16, VAL,
                            null, MAX,
                            -16, VAL,
                            "tooltip.spectrobesreforged.mineral.uranium"));

    public static final DeferredItem<Item> TITANIUM =
            ITEMS.register("titanium",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            -16, VAL,
                            -16, VAL,
                            null, MAX,
                            "tooltip.spectrobesreforged.mineral.titanium"));

    public static final DeferredItem<Item> CHROME =
            ITEMS.register("chrome",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            null, MAX,
                            -16, VAL,
                            -16, VAL,
                            "tooltip.spectrobesreforged.mineral.chrome"));

    public static final DeferredItem<Item> SAPPHIRE =
            ITEMS.register("sapphire",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            24, VAL, 24, VAL, 24, VAL,
                            "tooltip.spectrobesreforged.mineral.sapphire"));

    public static final DeferredItem<Item> EMERALD =
            ITEMS.register("emerald",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            36, VAL, 36, VAL, 36, VAL,
                            "tooltip.spectrobesreforged.mineral.emerald"));

    public static final DeferredItem<Item> RUBY =
            ITEMS.register("ruby",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            50, VAL, 50, VAL, 50, VAL,
                            "tooltip.spectrobesreforged.mineral.ruby"));

    public static final DeferredItem<Item> DIAMOND =
            ITEMS.register("diamond",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            0, VAL, 0, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.diamond"));

    public static final DeferredItem<Item> GOLD =
            ITEMS.register("gold",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            -15, VAL, -15, VAL, -15, VAL,
                            "tooltip.spectrobesreforged.mineral.gold"));

    public static final DeferredItem<Item> EVOLVE =
            ITEMS.register("evolve",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            0, VAL, 0, VAL, 0, VAL,
                            "tooltip.spectrobesreforged.mineral.evolve"));

    public static final DeferredItem<Item> CHROMA_0 =
            ITEMS.register("chroma_0",
                    () -> new ChromaMineralItem(
                            new Item.Properties().rarity(Rarity.RARE),
                            0
                    ));

    public static final DeferredItem<Item> CHROMA_1 =
            ITEMS.register("chroma_1",
                    () -> new ChromaMineralItem(
                            new Item.Properties().rarity(Rarity.RARE),
                            1
                    ));

    public static final DeferredItem<Item> CHROMA_2 =
            ITEMS.register("chroma_2",
                    () -> new ChromaMineralItem(
                            new Item.Properties().rarity(Rarity.RARE),
                            2
                    ));

    public static final List<DeferredItem<Item>> ALL_MINERALS = List.of(
            POWER_C, POWER_B, POWER_A, POWER_A_PLUS, DEFENSE_C, DEFENSE_B, DEFENSE_A, DEFENSE_A_PLUS, HEALTH_C, HEALTH_B, HEALTH_A, HEALTH_A_PLUS, AGATE, LAZULI, GRAPHITE, GARNET, ONYX, TOPAZ,
            JADE, AMBER, ZIRCON, OPAL, COBALT, CITRINE, FLUORITE, CORAL, SYLVITE, SPINAL, AZURITE, QUARTZ, URANIUM, TITANIUM, CHROME, PLATINUM, PEARL, SAPPHIRE, EMERALD, RUBY, DIAMOND, GOLD,
            EVOLVE, CHROMA_0, CHROMA_1, CHROMA_2
    );

    // Tools
    public static final DeferredItem<Item> WHITE_PRIZMOD =
            ITEMS.register("white_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> LIGHT_GRAY_PRIZMOD =
            ITEMS.register("light_gray_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GRAY_PRIZMOD =
            ITEMS.register("gray_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BLACK_PRIZMOD =
            ITEMS.register("black_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BROWN_PRIZMOD =
            ITEMS.register("brown_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> RED_PRIZMOD =
            ITEMS.register("red_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ORANGE_PRIZMOD =
            ITEMS.register("orange_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> YELLOW_PRIZMOD =
            ITEMS.register("yellow_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> LIME_PRIZMOD =
            ITEMS.register("lime_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GREEN_PRIZMOD =
            ITEMS.register("green_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> CYAN_PRIZMOD =
            ITEMS.register("cyan_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> LIGHT_BLUE_PRIZMOD =
            ITEMS.register("light_blue_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BLUE_PRIZMOD =
            ITEMS.register("blue_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> PURPLE_PRIZMOD =
            ITEMS.register("purple_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> MAGENTA_PRIZMOD =
            ITEMS.register("magenta_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> PINK_PRIZMOD =
            ITEMS.register("pink_prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BLOVA_SPAWN_EGG =
            ITEMS.register("blova_spawn_egg",
                    () -> new KrawlSpawnEggItem(
                            "blova",
                            new Item.Properties().stacksTo(64)
                    ));

    public static final DeferredItem<Item> VIBLOVA_SPAWN_EGG =
            ITEMS.register("viblova_spawn_egg",
                    () -> new KrawlSpawnEggItem(
                            "viblova",
                            new Item.Properties().stacksTo(64)
                    ));

    public static final DeferredItem<Item> BLOVANT_SPAWN_EGG =
            ITEMS.register("blovant_spawn_egg",
                    () -> new KrawlSpawnEggItem(
                            "blovant",
                            new Item.Properties().stacksTo(64)
                    ));

    public static final java.util.Map<String,DeferredItem<Item>> ENVIRONMENTAL_RAWS = environmentalRaws();
    private static java.util.Map<String,DeferredItem<Item>> environmentalRaws() {
        var entries=new java.util.LinkedHashMap<String,DeferredItem<Item>>();
        for(var deposit:com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationDeposits.all()) {
            entries.put(deposit.rawId(),ITEMS.register(deposit.rawId(),()->{
                var tag=new net.minecraft.nbt.CompoundTag();tag.putInt("ExcavationTerrain",deposit.terrain());tag.putInt("ExcavationRock",deposit.rock());
                var properties=new Item.Properties().component(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.of(tag));
                return deposit.fossil()?new RawFossilItem(SpectrobeType.valueOf(deposit.element().toUpperCase(java.util.Locale.ROOT)),properties):new RawMineralItem(deposit.grade(),properties);
            }));
        }
        return java.util.Collections.unmodifiableMap(entries);
    }
    public static final java.util.Map<String,DeferredItem<BlockItem>> ENVIRONMENTAL_ORES = environmentalOres();
    private static java.util.Map<String,DeferredItem<BlockItem>> environmentalOres() {
        var entries=new java.util.LinkedHashMap<String,DeferredItem<BlockItem>>();
        ModBlocks.ENVIRONMENTAL_ORES.forEach((id,block)->entries.put(id,ITEMS.registerSimpleBlockItem(id,block)));
        return java.util.Collections.unmodifiableMap(entries);
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
