// src/main/java/com/jvprz/spectrobesreforged/registry/ModItems.java
package com.jvprz.spectrobesreforged.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.content.SpectrobeType;
import com.jvprz.spectrobesreforged.content.item.FossilItem;

import com.jvprz.spectrobesreforged.content.item.PrizmodItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Rarity;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SpectrobesReforged.MODID);

    // 🔴 CORONA
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


    // 🟢 AURORA
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


    // 🔵 FLASH
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

    // Minerals
    public static final DeferredItem<Item> POWER_C =
            ITEMS.registerSimpleItem("power_c", new Item.Properties());

    public static final DeferredItem<Item> POWER_B =
            ITEMS.registerSimpleItem("power_b", new Item.Properties());

    public static final DeferredItem<Item> POWER_A =
            ITEMS.registerSimpleItem("power_a", new Item.Properties());

    public static final DeferredItem<Item> POWER_A_PLUS =
            ITEMS.registerSimpleItem("power_a_plus", new Item.Properties());

    public static final DeferredItem<Item> DEFENSE_C =
            ITEMS.registerSimpleItem("defense_c", new Item.Properties());

    public static final DeferredItem<Item> DEFENSE_B=
            ITEMS.registerSimpleItem("defense_b", new Item.Properties());

    public static final DeferredItem<Item> DEFENSE_A =
            ITEMS.registerSimpleItem("defense_a", new Item.Properties());

    public static final DeferredItem<Item> DEFENSE_A_PLUS =
            ITEMS.registerSimpleItem("defense_a_plus", new Item.Properties());

    public static final DeferredItem<Item> HEALTH_C =
            ITEMS.registerSimpleItem("health_c", new Item.Properties());

    public static final DeferredItem<Item> HEALTH_B =
            ITEMS.registerSimpleItem("health_b", new Item.Properties());

    public static final DeferredItem<Item> HEALTH_A =
            ITEMS.registerSimpleItem("health_a", new Item.Properties());

    public static final DeferredItem<Item> HEALTH_A_PLUS =
            ITEMS.registerSimpleItem("health_a_plus", new Item.Properties());

    // spawn egg
    public static final DeferredItem<Item> KOMAINU_SPAWN_EGG =
            ITEMS.register("komainu_spawn_egg",
                    () -> new SpawnEggItem(
                            ModEntities.KOMAINU.get(),
                            0xC63A6D, // color base (ajusta)
                            0x44D7D2, // manchas (ajusta)
                            new Item.Properties()
                    )
            );

    public static final DeferredItem<Item> PRIZMOD =
            ITEMS.register("prizmod",
                    () -> new PrizmodItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}