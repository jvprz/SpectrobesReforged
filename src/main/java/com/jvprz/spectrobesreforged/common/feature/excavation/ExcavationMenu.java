package com.jvprz.spectrobesreforged.common.feature.excavation;

import com.jvprz.spectrobesreforged.common.content.block.entity.ExcavationTableBlockEntity;
import com.jvprz.spectrobesreforged.common.content.item.RawFossilItem;
import com.jvprz.spectrobesreforged.common.registry.ModMenus;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class ExcavationMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;
    private boolean playerInventoryVisible = true;
    private java.util.List<ExcavationCoop.Cursor> coop=java.util.List.of();
    public void setCoop(java.util.List<ExcavationCoop.Cursor> cursors){coop=java.util.List.copyOf(cursors);}
    public java.util.List<ExcavationCoop.Cursor> coop(){return coop;}
    public ExcavationCoop.Cursor toolOwner(int tool){return coop.stream().filter(c->c.using()&&c.tool()==tool).findFirst().orElse(null);}
    public void showPlayerInventory(boolean visible) { playerInventoryVisible = visible; }
    public boolean playerInventoryVisible() { return playerInventoryVisible; }
    public ExcavationMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(2), new SimpleContainerData(ExcavationTableBlockEntity.DATA_COUNT));
    }
    public ExcavationMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.EXCAVATION_MENU.get(), id);
        this.container = container; this.data = data;
        checkContainerSize(container, 2); checkContainerDataCount(data, ExcavationTableBlockEntity.DATA_COUNT);
        addSlot(new Slot(container, 0, 12, 41) {
            @Override public boolean mayPlace(ItemStack stack) { return ExcavationSpecimen.accepts(stack); }
            @Override public int getMaxStackSize() { return 1; }
        });
        addSlot(new Slot(container, 1, 42, 41) { @Override public boolean mayPlace(ItemStack stack) { return false; } });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 88 + column * 18, 134 + row * 18) { @Override public boolean isActive() { return playerInventoryVisible; } });
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 88 + column * 18, 192) { @Override public boolean isActive() { return playerInventoryVisible; } });
        addDataSlots(data);
    }
    public ExcavationTableBlockEntity table() { return container instanceof ExcavationTableBlockEntity table ? table : null; }
    public int value(int index) { return data.get(index); }
    public int cell(int index) { return data.get(ExcavationTableBlockEntity.HEADERS + index); }
    @Override public boolean stillValid(Player player) { return container.stillValid(player); }
    @Override public void removed(Player player) { super.removed(player); if (table() != null) table().release(player); }
    @Override public boolean clickMenuButton(Player player, int button) {
        if(!player.isAlive()||player.isSpectator()||!stillValid(player))return false;
        return button == 0 && table() != null && table().extract(player);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem();
        if (index == 0 && table() != null) table().flush();
        ItemStack copy = original.copy();
        if (index < 2) {
            if (!moveItemStackTo(original, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else if (ExcavationSpecimen.accepts(original)) {
            if (!moveItemStackTo(original, 0, 1, false)) return ItemStack.EMPTY;
        } else return ItemStack.EMPTY;
        if (original.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, original);
        return copy;
    }
}
