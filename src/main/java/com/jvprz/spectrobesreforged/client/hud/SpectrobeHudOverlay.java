package com.jvprz.spectrobesreforged.client.hud;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.config.ClientConfig;
import com.jvprz.spectrobesreforged.client.feature.prizmod.ClientPrizmodState;
import com.jvprz.spectrobesreforged.client.input.ModKeybinds;
import com.jvprz.spectrobesreforged.client.ui.icon.SpectrobeIcons;
import com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels;
import com.jvprz.spectrobesreforged.common.feature.training.SpectrobeNames;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Exploration companion and six future combat slots, using the server's existing snapshot. */
@EventBusSubscriber(modid = SpectrobesReforged.MODID, value = Dist.CLIENT)
public final class SpectrobeHudOverlay {
    private static final int WIDTH = 166, HEIGHT = 94;
    private static final int TEXT = 0xffe6eff5, MUTED = 0xff92a6b5, EDGE = 0xb34b6475;
    private SpectrobeHudOverlay() {}

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (!ClientConfig.hudEnabled() || mc.player == null || mc.level == null || mc.options.hideGui || mc.player.isSpectator()
                || mc.screen != null || mc.getDebugOverlay().showDebugScreen()) return;
        var child = ClientPrizmodState.BABY;
        if (child != null && !"CHILD".equalsIgnoreCase(child.stage())) child = null;
        int occupied = 0;
        for (int i = 0; i < 6; i++) if (team(i) != null) occupied++;
        if (child == null && occupied == 0) return;

        var g = event.getGuiGraphics();
        // A dedicated corner avoids vanilla health, hunger, hotbar, chat and the raid bar.
        float scale = ClientConfig.hudScale();
        int panelHeight = ClientConfig.hudHeight();
        float px = 8 + Math.max(0, mc.getWindow().getGuiScaledWidth() - 16 - WIDTH * scale) * ClientConfig.hudX() / 100f;
        float py = 8 + Math.max(0, mc.getWindow().getGuiScaledHeight() - 16 - panelHeight * scale) * ClientConfig.hudY() / 100f;
        g.pose().pushPose();
        g.pose().translate(px, py, 0);
        g.pose().scale(scale, scale, 1);
        int x = 0, y = 0;
        g.fill(x + 2, y + 2, x + WIDTH + 2, y + panelHeight + 2, 0x50000000);
        g.fill(x, y, x + WIDTH, y + panelHeight, 0xd0111b24);
        g.renderOutline(x, y, WIDTH, panelHeight, EDGE);
        g.fill(x + 1, y + 1, x + WIDTH - 1, y + 17, 0xc01d2e3b);
        g.drawString(mc.font, "PRIZMOD", x + 9, y + 5, 0xffb8d9e9, false);
        String key = mc.font.plainSubstrByWidth(ModKeybinds.OPEN_PRIZMOD.getTranslatedKeyMessage().getString(), 40);
        int keyWidth = Math.max(12, mc.font.width(key) + 6);
        g.fill(x + WIDTH - keyWidth - 6, y + 3, x + WIDTH - 6, y + 14, 0x70476576);
        g.drawString(mc.font, key, x + WIDTH - keyWidth - 3, y + 5, TEXT, false);

        int accent = child == null ? MUTED : elementColor(child.element());
        g.fill(x + 8, y + 22, x + 42, y + 56, 0xa0081119);
        g.fill(x + 8, y + 54, x + 42, y + 56, accent);
        if (child != null) {
            portrait(g, child, x + 10, y + 24, 30);
            String name = mc.font.plainSubstrByWidth(SpectrobeNames.name(child.species(), false).getString(), 83);
            g.drawString(mc.font, name, x + 49, y + 24, TEXT, false);
            int badgeX = x + 52 + mc.font.width(name);
            if (child.crowned()) { g.drawString(mc.font, "♛", badgeX, y + 24, 0xffffd16d, false); badgeX += 10; }
            if (child.cooperative()) g.drawString(mc.font, "⚒", badgeX, y + 24, 0xff7ee8e3, false);
            g.drawString(mc.font, mc.font.plainSubstrByWidth(Component.translatable("gui.spectrobesreforged.hud.child_level", child.level()).getString(), 107), x + 49, y + 37, MUTED, false);
            int required = SpectrobeLevels.required(child.level());
            float progress = required == 0 ? 1 : Mth.clamp(child.xp() / (float) required, 0, 1);
            g.fill(x + 49, y + 50, x + WIDTH - 10, y + 53, 0xff283b47);
            g.fill(x + 49, y + 50, x + 49 + Math.round(107 * progress), y + 53, accent);
        } else {
            g.drawString(mc.font, "—", x + 21, y + 34, MUTED, false);
            g.drawString(mc.font, Component.translatable("gui.spectrobesreforged.hud.no_child"), x + 49, y + 30, MUTED, false);
        }

        if (ClientConfig.hudTeam()) {
        g.drawString(mc.font, Component.translatable("gui.spectrobesreforged.hud.team", occupied), x + 9, y + 59, MUTED, false);
        for (int i = 0; i < 6; i++) {
            int sx = x + 9 + (ClientConfig.hudVertical() ? 0 : i * 25), sy = y + 70 + (ClientConfig.hudVertical() ? i * 24 : 0);
            int slotWidth = ClientConfig.hudVertical() ? 148 : 22;
            var member = team(i);
            g.fill(sx, sy, sx + slotWidth, sy + 19, member == null ? 0x70303d48 : 0xd0203442);
            g.renderOutline(sx, sy, slotWidth, 19, member == null ? 0x60465b69 : elementColor(member.element()));
            if (member == null) {
                g.drawString(mc.font, Integer.toString(i + 1), sx + 8, sy + 5, 0xff697e8d, false);
            } else {
                portrait(g, member, sx + 3, sy + 1, 16);
                if (ClientConfig.hudVertical()) g.drawString(mc.font, mc.font.plainSubstrByWidth(SpectrobeNames.name(member.species(), false).getString(), 116), sx + 25, sy + 5, TEXT, false);
                float health = member.hp() <= 0 ? 0 : Mth.clamp(member.hpCur() / (float) member.hp(), 0, 1);
                g.fill(sx + 2, sy + 16, sx + slotWidth - 2, sy + 18, 0xff122029);
                g.fill(sx + 2, sy + 16, sx + 2 + Math.round((slotWidth - 4) * health), sy + 18, health > .25F ? 0xff75cbb3 : 0xffe78e85);
            }
        }
        }
        g.pose().popPose();
    }

    private static ClientPrizmodState.Entry team(int index) {
        if (index >= ClientPrizmodState.TEAM.size()) return null;
        var entry = ClientPrizmodState.TEAM.get(index);
        return entry != null && ("ADULT".equalsIgnoreCase(entry.stage()) || "EVOLVED".equalsIgnoreCase(entry.stage())) ? entry : null;
    }
    private static int elementColor(String element) {
        return switch (element) { case "corona" -> 0xffe7a16e; case "aurora" -> 0xff83c9a5; case "flash" -> 0xff80bde8; default -> 0xff9bb3c6; };
    }
    private static void portrait(GuiGraphics g, ClientPrizmodState.Entry entry, int x, int y, int size) {
        var mc = Minecraft.getInstance();
        var icon = SpectrobeIcons.icon(entry.species(), entry.color());
        if (mc.getResourceManager().getResource(icon).isPresent()) g.blit(icon, x, y, 0, 0, size, size, size, size);
        else g.drawString(mc.font, "?", x + size / 2 - 2, y + size / 2 - 4, MUTED, false);
    }
}
