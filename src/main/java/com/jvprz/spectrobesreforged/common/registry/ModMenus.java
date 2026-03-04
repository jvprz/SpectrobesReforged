package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.prizmod.menu.PrizmodMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    private ModMenus() {}

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.MENU, SpectrobesReforged.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<PrizmodMenu>> PRIZMOD_MENU =
            MENUS.register("prizmod_menu",
                    () -> new MenuType<>(PrizmodMenu::new, FeatureFlags.DEFAULT_FLAGS)
            );

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}