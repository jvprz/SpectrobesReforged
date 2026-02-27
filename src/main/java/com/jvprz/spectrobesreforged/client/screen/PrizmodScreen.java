package com.jvprz.spectrobesreforged.client.screen;

import com.jvprz.spectrobesreforged.client.prizmod.ClientPrizmodState;
import com.jvprz.spectrobesreforged.client.prizmod.SpectrobeIconRegistry;
import com.jvprz.spectrobesreforged.content.item.PrizmodMenu;
import com.jvprz.spectrobesreforged.network.C2SMoveSpectrobe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class PrizmodScreen extends AbstractContainerScreen<PrizmodMenu> {

    private static final int SLOT = 24;
    private static final int PAD = 4;

    private static final int BOX_COLS = 6;
    private static final int BOX_ROWS = 4;

    private static final int TEAM_COLS = 2;
    private static final int TEAM_ROWS = 3;

    // Layout cached per-frame (for hit-testing)
    private int invX0, invY0, teamX0, teamY0, babyX, babyY;

    // Drag state
    private boolean dragging = false;
    private int dragFromType = -1;   // 0=BOX, 1=TEAM, 2=BABY
    private int dragFromIndex = -1;  // index inside type (baby is always 0)
    private ClientPrizmodState.Entry dragEntry = null;

    public PrizmodScreen(PrizmodMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 320;
        this.imageHeight = 210;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {

        int baseX = leftPos;
        int baseY = topPos;

        // Background panel
        g.fill(baseX, baseY, baseX + imageWidth, baseY + imageHeight, 0xCC0B0B0B);
        g.fill(baseX + 1, baseY + 1, baseX + imageWidth - 1, baseY + imageHeight - 1, 0xCC1A1A1A);

        // Titles
        g.drawString(font, "PRIZMOD INVENTORY", baseX + 10, baseY + 8, 0xFFFFFF);
        g.drawString(font, "TEAM", baseX + 210, baseY + 8, 0xFFFFFF);
        g.drawString(font, "BABY", baseX + 210, baseY + 140, 0xFFFFFF);

        // Cache layout positions for hit-testing
        invX0 = baseX + 10;
        invY0 = baseY + 22;

        teamX0 = baseX + 210;
        teamY0 = baseY + 22;

        babyX = baseX + 220;
        babyY = baseY + 155;

        // --- INVENTORY GRID (6x4) ---
        for (int r = 0; r < BOX_ROWS; r++) {
            for (int c = 0; c < BOX_COLS; c++) {

                int sx = invX0 + c * (SLOT + PAD);
                int sy = invY0 + r * (SLOT + PAD);

                int idx = r * BOX_COLS + c;
                boolean selected = dragging && dragFromType == 0 && dragFromIndex == idx;

                drawSlot(g, sx, sy, selected);

                ClientPrizmodState.Entry entry =
                        idx < ClientPrizmodState.BOX.size() ? ClientPrizmodState.BOX.get(idx) : null;

                // If we are dragging this exact entry, don't draw it in-place (feels like "picked up")
                if (!(selected && dragEntry != null)) {
                    drawEntry(g, entry, sx, sy);
                }
            }
        }

        // --- TEAM GRID (2x3) ---
        for (int r = 0; r < TEAM_ROWS; r++) {
            for (int c = 0; c < TEAM_COLS; c++) {

                int sx = teamX0 + c * (SLOT + PAD);
                int sy = teamY0 + r * (SLOT + PAD);

                int idx = r * TEAM_COLS + c;
                boolean selected = dragging && dragFromType == 1 && dragFromIndex == idx;

                drawSlot(g, sx, sy, selected);

                ClientPrizmodState.Entry entry =
                        idx < ClientPrizmodState.TEAM.size() ? ClientPrizmodState.TEAM.get(idx) : null;

                if (!(selected && dragEntry != null)) {
                    drawEntry(g, entry, sx, sy);
                }
            }
        }

        // --- BABY SLOT (1) ---
        boolean babySelected = dragging && dragFromType == 2 && dragFromIndex == 0;
        drawSlot(g, babyX, babyY, babySelected);

        ClientPrizmodState.Entry baby = ClientPrizmodState.BABY;
        if (!(babySelected && dragEntry != null)) {
            drawEntry(g, baby, babyX, babyY);
        }
    }

    private void drawSlot(GuiGraphics g, int x, int y, boolean selected) {
        g.fill(x, y, x + SLOT, y + SLOT, 0xCC000000);
        g.fill(x + 1, y + 1, x + SLOT - 1, y + SLOT - 1, 0xCC2A2A2A);

        if (selected) {
            g.fill(x, y, x + SLOT, y + SLOT, 0x55FFFF00);
        }
    }

    private void drawEntry(GuiGraphics g, ClientPrizmodState.Entry entry, int x, int y) {
        if (entry == null) return;

        String species = entry.species() == null ? "" : entry.species();

        // Only blit icons we actually have (avoid missing-texture for now)
        if (species.equalsIgnoreCase("komainu")) {
            var tex = SpectrobeIconRegistry.icon(species);
            g.blit(tex, x + 4, y + 4, 0, 0, 16, 16, 16, 16);
            return;
        }

        String label = species.toLowerCase();
        if (label.length() > 6) label = label.substring(0, 6);
        g.drawString(font, label, x + 3, y + 8, 0xFFFFFF);
    }

    private boolean isInside(double mx, double my, int x, int y) {
        return mx >= x && mx <= x + SLOT &&
                my >= y && my <= y + SLOT;
    }

    private ClientPrizmodState.Entry getEntry(int type, int index) {
        return switch (type) {
            case 0 -> (index >= 0 && index < ClientPrizmodState.BOX.size()) ? ClientPrizmodState.BOX.get(index) : null;
            case 1 -> (index >= 0 && index < ClientPrizmodState.TEAM.size()) ? ClientPrizmodState.TEAM.get(index) : null;
            case 2 -> ClientPrizmodState.BABY;
            default -> null;
        };
    }

    private int hitBoxIndex(double mx, double my) {
        for (int r = 0; r < BOX_ROWS; r++) {
            for (int c = 0; c < BOX_COLS; c++) {
                int sx = invX0 + c * (SLOT + PAD);
                int sy = invY0 + r * (SLOT + PAD);
                if (isInside(mx, my, sx, sy)) return r * BOX_COLS + c;
            }
        }
        return -1;
    }

    private int hitTeamIndex(double mx, double my) {
        for (int r = 0; r < TEAM_ROWS; r++) {
            for (int c = 0; c < TEAM_COLS; c++) {
                int sx = teamX0 + c * (SLOT + PAD);
                int sy = teamY0 + r * (SLOT + PAD);
                if (isInside(mx, my, sx, sy)) return r * TEAM_COLS + c;
            }
        }
        return -1;
    }

    private boolean hitBaby(double mx, double my) {
        return isInside(mx, my, babyX, babyY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int bi = hitBoxIndex(mouseX, mouseY);
        if (bi != -1) {
            var e = getEntry(0, bi);
            if (e != null) {
                dragging = true;
                dragFromType = 0;
                dragFromIndex = bi;
                dragEntry = e;
            }
            return true;
        }

        int ti = hitTeamIndex(mouseX, mouseY);
        if (ti != -1) {
            var e = getEntry(1, ti);
            if (e != null) {
                dragging = true;
                dragFromType = 1;
                dragFromIndex = ti;
                dragEntry = e;
            }
            return true;
        }

        if (hitBaby(mouseX, mouseY)) {
            var e = getEntry(2, 0);
            if (e != null) {
                dragging = true;
                dragFromType = 2;
                dragFromIndex = 0;
                dragEntry = e;
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging) {

            int toType = -1;
            int toIndex = -1;

            int bi = hitBoxIndex(mouseX, mouseY);
            if (bi != -1) { toType = 0; toIndex = bi; }

            int ti = hitTeamIndex(mouseX, mouseY);
            if (ti != -1) { toType = 1; toIndex = ti; }

            if (hitBaby(mouseX, mouseY)) { toType = 2; toIndex = 0; }


            if (toType != -1 && !(toType == dragFromType && toIndex == dragFromIndex)) {
                 PacketDistributor.sendToServer(new C2SMoveSpectrobe(dragFromType, dragFromIndex, toType, toIndex));
             }

            dragging = false;
            dragFromType = -1;
            dragFromIndex = -1;
            dragEntry = null;

            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);

        // Draw dragged icon/text on cursor
        if (dragging && dragEntry != null) {
            String species = dragEntry.species();

            if (species != null && species.equalsIgnoreCase("komainu")) {
                var tex = SpectrobeIconRegistry.icon(species);
                g.blit(tex, mouseX - 8, mouseY - 8, 0, 0, 16, 16, 16, 16);
            } else if (species != null) {
                String label = species.toLowerCase();
                if (label.length() > 6) label = label.substring(0, 6);
                g.drawString(font, label, mouseX - 10, mouseY - 4, 0xFFFFFF);
            }
        }

        this.renderTooltip(g, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        // no vanilla labels
    }
}