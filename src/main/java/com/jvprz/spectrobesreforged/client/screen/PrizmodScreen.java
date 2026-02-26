package com.jvprz.spectrobesreforged.client.screen;

import com.jvprz.spectrobesreforged.content.item.PrizmodMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PrizmodScreen extends AbstractContainerScreen<PrizmodMenu> {

    public PrizmodScreen(PrizmodMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Fondo vacío por ahora
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, "Prizmod System", leftPos + 10, topPos + 10, 0xFFFFFF);
    }
}