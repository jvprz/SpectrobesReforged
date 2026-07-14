package com.jvprz.spectrobesreforged.client.screen;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.incubator.menu.IncubatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IncubatorScreen extends AbstractContainerScreen<IncubatorMenu> {

    private static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    SpectrobesReforged.MODID,
                    "textures/gui/incubator.png"
            );

    private static final ResourceLocation START_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    SpectrobesReforged.MODID,
                    "textures/gui/start_button.png"
            );

    private static final ResourceLocation PROGRESS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    SpectrobesReforged.MODID,
                    "textures/gui/progress.png"
            );

    private static final ResourceLocation FREQUENCY_LEDS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    SpectrobesReforged.MODID,
                    "textures/gui/frequency_leds.png"
            );

    // Botón START
    private static final int START_BUTTON_X = 61;
    private static final int START_BUTTON_Y = 85;
    private static final int START_BUTTON_WIDTH = 80;
    private static final int START_BUTTON_HEIGHT = 20;

    private static final int START_BUTTON_NORMAL_V = 0;
    private static final int START_BUTTON_HOVER_V = 20;
    private static final int START_BUTTON_DISABLED_V = 40;

    private static final int START_BUTTON_TEXTURE_WIDTH = 80;
    private static final int START_BUTTON_TEXTURE_HEIGHT = 60;

    // Barra de progreso
    private static final int PROGRESS_X = 15;
    private static final int PROGRESS_Y = 51;
    private static final int PROGRESS_WIDTH = 8;
    private static final int PROGRESS_HEIGHT = 60;
    private static final int MAX_PROGRESS = 200;

    // Indicador de frecuencia
    private static final int LEDS_X = 53;
    private static final int LEDS_Y = 73;

    private static final int LED_WIDTH = 6;
    private static final int LED_HEIGHT = 12;
    private static final int LED_COUNT = 16;

    private static final int LED_TEXTURE_WIDTH = 96;
    private static final int LED_TEXTURE_HEIGHT = 24;

    private static final int LED_ON_V = 0;
    private static final int LED_OFF_V = 12;

    public IncubatorScreen(
            IncubatorMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        renderBackgroundTexture(guiGraphics);
        renderStartButton(guiGraphics, mouseX, mouseY);
        renderProgressBar(guiGraphics);
        renderFrequencyLeds(guiGraphics);
    }

    private void renderBackgroundTexture(GuiGraphics guiGraphics) {
        guiGraphics.blit(
                BACKGROUND_TEXTURE,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                this.imageWidth,
                this.imageHeight
        );
    }

    private void renderStartButton(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY
    ) {
        int buttonX = this.leftPos + START_BUTTON_X;
        int buttonY = this.topPos + START_BUTTON_Y;

        boolean enabled = this.menu.canStart();
        boolean hovered = isMouseOverStartButton(mouseX, mouseY);

        int textureV;

        if (!enabled) {
            textureV = START_BUTTON_DISABLED_V;
        } else if (hovered) {
            textureV = START_BUTTON_HOVER_V;
        } else {
            textureV = START_BUTTON_NORMAL_V;
        }

        guiGraphics.blit(
                START_BUTTON_TEXTURE,
                buttonX,
                buttonY,
                0,
                textureV,
                START_BUTTON_WIDTH,
                START_BUTTON_HEIGHT,
                START_BUTTON_TEXTURE_WIDTH,
                START_BUTTON_TEXTURE_HEIGHT
        );
    }

    private void renderProgressBar(GuiGraphics guiGraphics) {
        int progress = Math.max(
                0,
                Math.min(MAX_PROGRESS, this.menu.getAwakeningProgress())
        );

        int filledHeight =
                progress * PROGRESS_HEIGHT / MAX_PROGRESS;

        if (filledHeight <= 0) {
            return;
        }

        guiGraphics.blit(
                PROGRESS_TEXTURE,
                this.leftPos + PROGRESS_X,
                this.topPos + PROGRESS_Y
                        + (PROGRESS_HEIGHT - filledHeight),
                0,
                PROGRESS_HEIGHT - filledHeight,
                PROGRESS_WIDTH,
                filledHeight,
                PROGRESS_WIDTH,
                PROGRESS_HEIGHT
        );
    }

    private void renderFrequencyLeds(GuiGraphics guiGraphics) {
        int capturedFrequency = Math.max(
                0,
                Math.min(
                        LED_COUNT,
                        this.menu.getCapturedFrequency()
                )
        );

        for (int ledIndex = 0; ledIndex < LED_COUNT; ledIndex++) {
            boolean lit = ledIndex < capturedFrequency;

            int textureU = ledIndex * LED_WIDTH;
            int textureV = lit ? LED_ON_V : LED_OFF_V;

            int ledX =
                    this.leftPos
                            + LEDS_X
                            + ledIndex * LED_WIDTH;

            int ledY = this.topPos + LEDS_Y;

            guiGraphics.blit(
                    FREQUENCY_LEDS_TEXTURE,
                    ledX,
                    ledY,
                    textureU,
                    textureV,
                    LED_WIDTH,
                    LED_HEIGHT,
                    LED_TEXTURE_WIDTH,
                    LED_TEXTURE_HEIGHT
            );
        }
    }

    private boolean isMouseOverStartButton(
            double mouseX,
            double mouseY
    ) {
        int buttonX = this.leftPos + START_BUTTON_X;
        int buttonY = this.topPos + START_BUTTON_Y;

        return mouseX >= buttonX
                && mouseX < buttonX + START_BUTTON_WIDTH
                && mouseY >= buttonY
                && mouseY < buttonY + START_BUTTON_HEIGHT;
    }

    @Override
    protected void renderLabels(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY
    ) {
        guiGraphics.drawString(
                this.font,
                this.title,
                8,
                6,
                0x404040,
                false
        );

        guiGraphics.drawString(
                this.font,
                this.playerInventoryTitle,
                8,
                128,
                0x404040,
                false
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button == 0
                && isMouseOverStartButton(mouseX, mouseY)
                && this.menu.canStart()
                && this.minecraft != null
                && this.minecraft.gameMode != null) {

            this.minecraft.gameMode.handleInventoryButtonClick(
                    this.menu.containerId,
                    IncubatorMenu.START_BUTTON_ID
            );

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        this.renderBackground(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );

        super.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );

        this.renderTooltip(
                guiGraphics,
                mouseX,
                mouseY
        );
    }
}