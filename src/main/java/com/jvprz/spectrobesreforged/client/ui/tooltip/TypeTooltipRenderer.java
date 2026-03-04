package com.jvprz.spectrobesreforged.client.ui.tooltip;

import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeType;
import com.jvprz.spectrobesreforged.common.ui.tooltip.TypeTooltipComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;

public class TypeTooltipRenderer implements ClientTooltipComponent {

    private final SpectrobeType type;

    public TypeTooltipRenderer(TypeTooltipComponent component) {
        this.type = component.type();
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public int getWidth(Font font) {
        return 16;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                "spectrobesreforged",
                "textures/gui/type/" + type.name().toLowerCase() + ".png"
        );

        // 16x16 exacto
        guiGraphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16);
    }
}