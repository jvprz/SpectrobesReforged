package com.jvprz.spectrobesreforged.common.feature.prizmod.menu;

import com.jvprz.spectrobesreforged.common.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class PrizmodMenu extends AbstractContainerMenu {

    public PrizmodMenu(int id, Inventory inventory) {
        super(ModMenus.PRIZMOD_MENU.get(), id);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}