// src/main/java/com/jvprz/spectrobesreforged/registry/ModItems.java
package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.item.ChromaMineralItem;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeType;
import com.jvprz.spectrobesreforged.common.content.item.FossilItem;

import com.jvprz.spectrobesreforged.common.content.item.MineralItem;
import com.jvprz.spectrobesreforged.common.content.item.PrizmodItem;
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
                            "This mineral increases Attack Minergy by 8! It makes Spectrobe attacks slightly stronger."));

    public static final DeferredItem<Item> POWER_B =
            ITEMS.register("power_b",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            0, VAL, 16, VAL, 0, VAL,
                            "This mineral increases Attack Minergy by 16! It gives Spectrobes increased offensive power."));

    public static final DeferredItem<Item> DEFENSE_C =
            ITEMS.register("defense_c",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            0, VAL, 0, VAL, 8, VAL,
                            "This mineral increases Defense Minergy by 8! It gives Spectrobes slightly better battle protection."));

    public static final DeferredItem<Item> DEFENSE_B =
            ITEMS.register("defense_b",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            0, VAL, 0, VAL, 16, VAL,
                            "This mineral increases Defense Minergy by 16! It allows Spectrobes to resist heavier enemy blows!"));

    public static final DeferredItem<Item> HEALTH_C =
            ITEMS.register("health_c",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            8, VAL, 0, VAL, 0, VAL,
                            "This mineral increases Health Minergy by 8! It gives Spectrobes slightly better endurance."));

    public static final DeferredItem<Item> HEALTH_B =
            ITEMS.register("health_b",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.COMMON),
                            16, VAL, 0, VAL, 0, VAL,
                            "This mineral increases Health Minergy by 16! It gives Spectrobes pretty good battle stamina."));

    // Minerals tier uncommon
    public static final DeferredItem<Item> POWER_A =
            ITEMS.register("power_a",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 24, VAL, 0, VAL,
                            "This mineral increases Attack Minergy by 24! It gives Spectrobes real battle damage power!"));

    public static final DeferredItem<Item> POWER_A_PLUS =
            ITEMS.register("power_a_plus",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 32, VAL, 0, VAL,
                            "This mineral increases Attack Minergy by 32! It gives Spectrobes formidable warrior power!"));

    public static final DeferredItem<Item> DEFENSE_A =
            ITEMS.register("defense_a",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 0, VAL, 24, VAL,
                            "This mineral increases Defense Minergy by 24! It allows Spectrobes to face more dangerous foes."));

    public static final DeferredItem<Item> DEFENSE_A_PLUS =
            ITEMS.register("defense_a_plus",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 0, VAL, 32, VAL,
                            "This mineral increases Defense Minergy by 32! It allows Spectrobes to withstand ferocious blows!"));

    public static final DeferredItem<Item> HEALTH_A =
            ITEMS.register("health_a",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            24, VAL, 0, VAL, 0, VAL,
                            "This mineral increases Health Minergy by 24! It gives Spectrobes elevated battle endurance!"));

    public static final DeferredItem<Item> HEALTH_A_PLUS =
            ITEMS.register("health_a_plus",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            32, VAL, 0, VAL, 0, VAL,
                            "This mineral increases Health Minergy by 32! It gives Spectrobes amazing battle stamina!"));

    public static final DeferredItem<Item> AGATE =
            ITEMS.register("agate",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 8, VAL, 8, VAL,
                            "This mineral increases Attack Minergy and Defense Minergy by 8! It's a useful supplement."));

    public static final DeferredItem<Item> LAZULI =
            ITEMS.register("lazuli",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            0, VAL, 16, VAL, 16, VAL,
                            "This mineral increases Attack Minergy and Defense Minergy by 16! It's a great supplement."));

    public static final DeferredItem<Item> JADE =
            ITEMS.register("jade",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            8, VAL, 8, VAL, 0, VAL,
                            "This mineral increases Attack Minergy and Health Minergy by 8! It's a useful supplement!"));

    public static final DeferredItem<Item> AMBER =
            ITEMS.register("amber",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            16, VAL, 16, VAL, 0, VAL,
                            "This mineral increases Attack Minergy and Health Minergy by 16! It's a great supplement!"));

    public static final DeferredItem<Item> FLUORITE =
            ITEMS.register("fluorite",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            8, VAL, 0, VAL, 8, VAL,
                            "This mineral increases Defense Minergy and Health Minergy by 8! It's rather useful."));

    public static final DeferredItem<Item> CORAL =
            ITEMS.register("coral",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.UNCOMMON),
                            16, VAL, 0, VAL, 16, VAL,
                            "This mineral increases Defense Minergy and Health Minergy by 16! It's quite useful."));

    // Minerals tier rare
    public static final DeferredItem<Item> GRAPHITE =
            ITEMS.register("graphite",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            0, VAL, 30, VAL, -8, VAL,
                            "This mineral increases Attack Minergy by 30 but decreases Defense Minergy by 8!"));

    public static final DeferredItem<Item> GARNET =
            ITEMS.register("garnet",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            0, VAL, -8, VAL, 30, VAL,
                            "This mineral reduces Attack Minergy by 8 but increases Defense Minergy by 30!"));

    public static final DeferredItem<Item> ONYX =
            ITEMS.register("onyx",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            0, VAL, 40, VAL, -16, VAL,
                            "This mineral increases Attack Minergy by 40 but decreases Defense Minergy by 16!"));

    public static final DeferredItem<Item> TOPAZ =
            ITEMS.register("topaz",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            0, VAL, -16, VAL, 40, VAL,
                            "This mineral reduces Attack Minergy by 16 but increases Defense Minergy by 40!"));

    public static final DeferredItem<Item> ZIRCON =
            ITEMS.register("zircon",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            -8, VAL, 30, VAL, 0, VAL,
                            "This mineral increases Attack Minergy by 30 but reduces Health Minergy by 8!"));

    public static final DeferredItem<Item> OPAL =
            ITEMS.register("opal",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            30, VAL, -8, VAL, 0, VAL,
                            "This mineral reduces Attack Minergy by 8 but increases Health Minergy by 30!"));

    public static final DeferredItem<Item> COBALT =
            ITEMS.register("cobalt",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            -16, VAL, 40, VAL, 0, VAL,
                            "This mineral increases Attack Minergy by 40 but reduces Health Minergy by 16!"));

    public static final DeferredItem<Item> CITRINE =
            ITEMS.register("citrine",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            40, VAL, -16, VAL, 0, VAL,
                            "This mineral reduces Attack Minergy by 16 but increases Health Minergy by 40!"));

    public static final DeferredItem<Item> SYLVITE =
            ITEMS.register("sylvite",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            -8, VAL, 0, VAL, 30, VAL,
                            "This mineral increases Defense Minergy by 30 but reduces Health Minergy by 8!"));

    public static final DeferredItem<Item> SPINAL =
            ITEMS.register("spinal",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            30, VAL, 0, VAL, -8, VAL,
                            "This mineral reduces Defense Minergy by 8 but increases Health Minergy by 30!"));

    public static final DeferredItem<Item> AZURITE =
            ITEMS.register("azurite",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            -16, VAL, 0, VAL, 40, VAL,
                            "This mineral increases Defense Minergy by 40 but reduces Health Minergy by 16!"));

    public static final DeferredItem<Item> QUARTZ =
            ITEMS.register("quartz",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            40, VAL, 0, VAL, -16, VAL,
                            "This mineral reduces Defense Minergy by 16 but increases Health Minergy by 40!"));

    public static final DeferredItem<Item> PLATINUM =
            ITEMS.register("platinum",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            5, VAL, 5, VAL, 5, VAL,
                            "This mineral increases Attack, Defense and Health Minergies by 5! It's not a bad find."));

    public static final DeferredItem<Item> PEARL =
            ITEMS.register("pearl",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            12, VAL, 12, VAL, 12, VAL,
                            "This mineral increases Attack, Defense and Health Minergies by 12! It's pretty handy."));

    // Minerals tier epic
    public static final DeferredItem<Item> URANIUM =
            ITEMS.register("uranium",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            -16, VAL,
                            null, MAX,
                            -16, VAL,
                            "This mineral increases Attack Minergy to the max but reduces all other Minergy by 16!"));

    public static final DeferredItem<Item> TITANIUM =
            ITEMS.register("titanium",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            -16, VAL,
                            -16, VAL,
                            null, MAX,
                            "This mineral increases Defense Minergy to the max but reduces all other Minergy by 16!"));

    public static final DeferredItem<Item> CHROME =
            ITEMS.register("chrome",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            null, MAX,
                            -16, VAL,
                            -16, VAL,
                            "This mineral increases Health Minergy to the max but reduces all other Minergy by 16!"));

    public static final DeferredItem<Item> SAPPHIRE =
            ITEMS.register("sapphire",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            24, VAL, 24, VAL, 24, VAL,
                            "This mineral increases Attack, Defense and Health Minergies by 24! It's quite useful."));

    public static final DeferredItem<Item> EMERALD =
            ITEMS.register("emerald",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            36, VAL, 36, VAL, 36, VAL,
                            "This mineral increases Attack, Defense and Health Minergies by 36! It's quite valuable."));

    public static final DeferredItem<Item> RUBY =
            ITEMS.register("ruby",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            50, VAL, 50, VAL, 50, VAL,
                            "This mineral increases Attack, Defense and Health Minergies by 50! It's a treasure!"));

    public static final DeferredItem<Item> DIAMOND =
            ITEMS.register("diamond",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            0, VAL, 0, VAL, 0, VAL,
                            "This rare gem of the Table Top Mountains has untold value. Be wary of its captivating beauty."));

    public static final DeferredItem<Item> GOLD =
            ITEMS.register("gold",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.RARE),
                            -15, VAL, -15, VAL, -15, VAL,
                            "This glittering gold mineral is considered a treasure among mineral hunters."));

    public static final DeferredItem<Item> EVOLVE =
            ITEMS.register("evolve",
                    () -> new MineralItem(new Item.Properties().rarity(Rarity.EPIC),
                            0, VAL, 0, VAL, 0, VAL,
                            "This unique mineral has the ability to evolve a Spectrobe!"));

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

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}