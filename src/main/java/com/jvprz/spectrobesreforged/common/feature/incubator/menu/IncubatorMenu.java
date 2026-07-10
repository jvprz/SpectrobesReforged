package com.jvprz.spectrobesreforged.common.feature.incubator.menu;

import com.jvprz.spectrobesreforged.common.content.item.FossilItem;
import com.jvprz.spectrobesreforged.common.content.block.entity.IncubatorBlockEntity;
import com.jvprz.spectrobesreforged.common.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

public class IncubatorMenu extends AbstractContainerMenu {

    private static final int INCUBATOR_SLOT_COUNT = 1;
    private static final int PLAYER_INVENTORY_START = 1;
    private static final int PLAYER_INVENTORY_END = 28;
    private static final int HOTBAR_START = 28;
    private static final int HOTBAR_END = 37;
    public static final int START_BUTTON_ID = 0;

    private final Container incubatorContainer;
    private final ContainerData data;


    public IncubatorMenu(int id, Inventory inventory) {
        this(
                id,
                inventory,
                new SimpleContainer(INCUBATOR_SLOT_COUNT),
                new SimpleContainerData(4)
        );
    }

    public IncubatorMenu(
            int id,
            Inventory inventory,
            Container incubatorContainer,
            ContainerData data
    ) {
        super(ModMenus.INCUBATOR_MENU.get(), id);

        checkContainerSize(
                incubatorContainer,
                INCUBATOR_SLOT_COUNT
        );
        checkContainerDataCount(data, 4);

        this.incubatorContainer = incubatorContainer;
        this.data = data;
        this.addDataSlots(data);

        this.addSlot(new Slot(
                incubatorContainer,
                0,
                80,
                58
        ) {
            @Override
            public boolean mayPickup(Player player) {
                if (incubatorContainer instanceof IncubatorBlockEntity incubator) {
                    return !incubator.isAwakening();
                }

                return true;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public int getMaxStackSize(ItemStack stack) {
                return 1;
            }
        });

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    public boolean isAwakening() {
        return this.data.get(0) != 0;
    }

    public int getAwakeningProgress() {
        return this.data.get(1);
    }

    public int getCapturedFrequency() {
        return this.data.get(2);
    }

    public int getSelectedColor() {
        return this.data.get(3);
    }

    public boolean canStart() {
        ItemStack fossilStack = this.getSlot(0).getItem();

        return !fossilStack.isEmpty()
                && fossilStack.getItem() instanceof FossilItem
                && !this.isAwakening();
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId != START_BUTTON_ID) {
            return false;
        }

        if (!this.canStart()) {
            return false;
        }

        if (this.incubatorContainer instanceof IncubatorBlockEntity incubator) {
            if (!incubator.startAwakening(player)) {
                return false;
            }

            player.displayClientMessage(
                    Component.translatable(
                            "message.spectrobesreforged.incubator.start_test"
                    ),
                    false
            );

            return true;
        }

        return false;
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(
                        inventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        140 + row * 18
                ));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(
                    inventory,
                    column,
                    8 + column * 18,
                    198
            ));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.incubatorContainer.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = this.slots.get(index);

        if (index == 0
                && this.incubatorContainer instanceof IncubatorBlockEntity incubator
                && incubator.isAwakening()) {
            return ItemStack.EMPTY;
        }

        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack originalStack = sourceStack.copy();

        if (index == 0) {
            if (!this.moveItemStackTo(
                    sourceStack,
                    PLAYER_INVENTORY_START,
                    HOTBAR_END,
                    true
            )) {
                return ItemStack.EMPTY;
            }
        }

        else if (sourceStack.getItem() instanceof FossilItem) {
            if (!this.moveItemStackTo(
                    sourceStack,
                    0,
                    INCUBATOR_SLOT_COUNT,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        else if (index >= PLAYER_INVENTORY_START
                && index < PLAYER_INVENTORY_END) {

            if (!this.moveItemStackTo(
                    sourceStack,
                    HOTBAR_START,
                    HOTBAR_END,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        else if (index >= HOTBAR_START
                && index < HOTBAR_END) {

            if (!this.moveItemStackTo(
                    sourceStack,
                    PLAYER_INVENTORY_START,
                    PLAYER_INVENTORY_END,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.setByPlayer(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        if (sourceStack.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        sourceSlot.onTake(player, sourceStack);
        return originalStack;
    }
}