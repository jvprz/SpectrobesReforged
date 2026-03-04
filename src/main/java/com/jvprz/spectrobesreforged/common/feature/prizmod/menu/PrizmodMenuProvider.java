package com.jvprz.spectrobesreforged.common.feature.prizmod.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class PrizmodMenuProvider implements MenuProvider {

    @Override
    public Component getDisplayName() {
        return Component.literal("Prizmod");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new PrizmodMenu(id, inventory);
    }
}