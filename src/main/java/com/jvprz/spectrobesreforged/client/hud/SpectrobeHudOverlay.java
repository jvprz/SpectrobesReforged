package com.jvprz.spectrobesreforged.client.hud;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.client.feature.prizmod.ClientPrizmodState;
import com.jvprz.spectrobesreforged.client.ui.icon.SpectrobeIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = SpectrobesReforged.MODID, value = Dist.CLIENT)
public final class SpectrobeHudOverlay {

    private static final ResourceLocation SLOT_TEX =
            ResourceLocation.fromNamespaceAndPath(
                    SpectrobesReforged.MODID,
                    "textures/gui/spectrobe_slot.png"
            );

    private SpectrobeHudOverlay() {}

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        ClientPrizmodState.Entry child = getEquippedChild();
        if (child == null) {
            return;
        }

        GuiGraphics gui = event.getGuiGraphics();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int hotbarX = (screenWidth - 182) / 2;
        int hotbarY = screenHeight - 22;

        int x = hotbarX + 182 + 9; // ← 1px más a la derecha
        int y = hotbarY + 1;

        // slot más transparente
        gui.setColor(1f, 1f, 1f, 0.45f);
        gui.blit(SLOT_TEX, x, y, 0, 0, 18, 18, 18, 18);
        gui.setColor(1f, 1f, 1f, 1f);

        ResourceLocation icon = SpectrobeIcons.icon(child.species(), child.color());
        gui.blit(icon, x + 1, y + 1, 0, 0, 16, 16, 16, 16);
    }

    private static ClientPrizmodState.Entry getEquippedChild() {

        ClientPrizmodState.Entry baby = ClientPrizmodState.BABY;

        if (baby == null) {
            return null;
        }

        if (!"CHILD".equalsIgnoreCase(baby.stage())) {
            return null;
        }

        return baby;
    }
}