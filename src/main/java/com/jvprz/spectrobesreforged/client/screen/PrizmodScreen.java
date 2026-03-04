package com.jvprz.spectrobesreforged.client.screen;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.client.feature.prizmod.ClientPrizmodState;
import com.jvprz.spectrobesreforged.client.ui.icon.SpectrobeIcons;
import com.jvprz.spectrobesreforged.common.feature.prizmod.menu.PrizmodMenu;
import com.jvprz.spectrobesreforged.common.network.C2SMoveSpectrobe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrizmodScreen extends AbstractContainerScreen<PrizmodMenu> {

    private static final int BOX_COLS = 6;
    private static final int BOX_ROWS = 4;

    private static final int TEAM_COLS = 2;
    private static final int TEAM_ROWS = 3;

    private static final int TYPE_NONE = -1;
    private static final int TYPE_BOX  = 0;
    private static final int TYPE_TEAM = 1;
    private static final int TYPE_BABY = 2;

    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, "textures/gui/prizmod.png");

    // tu textura final
    private static final int TEX_W = 174;
    private static final int TEX_H = 115;

    // hueco real (hitbox)
    private static final int SLOT_W = 16;
    private static final int SLOT_H = 16;

    // inicio-a-inicio entre slots
    private static final int PAD_X = 3;
    private static final int PAD_Y = 9;

    // offsets dentro del PNG
    private static final int INV_OX  = 6;
    private static final int INV_OY  = 17;

    private static final int TEAM_OX = 132;
    private static final int TEAM_OY = 17;

    private static final int BABY_OX = 132;
    private static final int BABY_OY = 92;

    // Layout cached per-frame (for hit-testing) en ABSOLUTO
    private int invX0, invY0, teamX0, teamY0, babyX, babyY;

    // “Hand” state (picked up)
    private boolean dragging = false;
    private int dragFromType = -1;
    private int dragFromIndex = -1;
    private ClientPrizmodState.Entry dragEntry = null;

    // Hold-drag (mouse kept pressed) support
    private boolean holdingMouseDrag = false;

    public PrizmodScreen(PrizmodMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = TEX_W;
        this.imageHeight = TEX_H;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {

        int baseX = leftPos;
        int baseY = topPos;

        // 1) Fondo (tu PNG 174x115)
        g.blit(TEX, baseX, baseY, 0, 0, imageWidth, imageHeight, TEX_W, TEX_H);

        g.drawString(font, "PRIZMOD",
                baseX + 7,
                baseY + 14 - font.lineHeight,
                0xc2f2b8);

        g.drawString(font, "TEAM",
                baseX + 131,
                baseY + 14 - font.lineHeight,
                0xc2f2b8);

        // Cache layout para hit-testing (coordenadas absolutas)
        invX0  = baseX + INV_OX;
        invY0  = baseY + INV_OY;

        teamX0 = baseX + TEAM_OX;
        teamY0 = baseY + TEAM_OY;

        babyX  = baseX + BABY_OX;
        babyY  = baseY + BABY_OY;

        // 2) INVENTORY GRID (6x4)
        for (int r = 0; r < BOX_ROWS; r++) {
            for (int c = 0; c < BOX_COLS; c++) {

                int sx = invX0 + c * (SLOT_W + PAD_X);
                int sy = invY0 + r * (SLOT_H + PAD_Y);

                int idx = r * BOX_COLS + c;
                boolean selected = dragging && dragFromType == TYPE_BOX && dragFromIndex == idx;

                drawSlotOverlay(g, sx, sy, selected);

                ClientPrizmodState.Entry entry =
                        idx < ClientPrizmodState.BOX.size() ? ClientPrizmodState.BOX.get(idx) : null;

                if (!(selected && dragEntry != null)) {
                    drawEntry16(g, entry, sx, sy);
                }
            }
        }

        // 3) TEAM GRID (2x3)
        for (int r = 0; r < TEAM_ROWS; r++) {
            for (int c = 0; c < TEAM_COLS; c++) {

                int sx = teamX0 + c * (SLOT_W + PAD_X);
                int sy = teamY0 + r * (SLOT_H + PAD_Y);

                int idx = r * TEAM_COLS + c;
                boolean selected = dragging && dragFromType == TYPE_TEAM && dragFromIndex == idx;

                drawSlotOverlay(g, sx, sy, selected);

                ClientPrizmodState.Entry entry =
                        idx < ClientPrizmodState.TEAM.size() ? ClientPrizmodState.TEAM.get(idx) : null;

                if (!(selected && dragEntry != null)) {
                    drawEntry16(g, entry, sx, sy);
                }
            }
        }

        // 4) BABY SLOT
        boolean babySelected = dragging && dragFromType == TYPE_BABY && dragFromIndex == 0;
        drawSlotOverlay(g, babyX, babyY, babySelected);

        ClientPrizmodState.Entry baby = ClientPrizmodState.BABY;
        if (!(babySelected && dragEntry != null)) {
            drawEntry16(g, baby, babyX, babyY);
        }
    }

    private void drawSlotOverlay(GuiGraphics g, int x, int y, boolean selected) {
        if (selected) {
            g.fill(x, y, x + SLOT_W, y + SLOT_H, 0x55FFFF00);
        }
    }

    /**
     * Dibuja SIEMPRE icono 16x16 centrado dentro del hueco (SLOT_W x SLOT_H).
     */
    private void drawEntry16(GuiGraphics g, ClientPrizmodState.Entry entry, int slotX, int slotY) {
        if (entry == null) return;

        String species = entry.species();
        if (species == null) return;

        int ix = slotX + (SLOT_W - 16) / 2;
        int iy = slotY + (SLOT_H - 16) / 2;

        if (species.equalsIgnoreCase("komainu")) {
            var tex = SpectrobeIcons.icon(species, entry.color());
            g.blit(tex, ix, iy, 0, 0, 16, 16, 16, 16);
            return;
        }

        String label = species.toLowerCase();
        if (label.length() > 6) label = label.substring(0, 6);
        g.drawString(font, label, slotX + 2, slotY + (SLOT_H / 2) - 4, 0xFFFFFF);
    }

    private boolean isInside(double mx, double my, int x, int y) {
        return mx >= x && mx <= x + SLOT_W &&
                my >= y && my <= y + SLOT_H;
    }

    private ClientPrizmodState.Entry getEntry(int type, int index) {
        return switch (type) {
            case TYPE_BOX -> (index >= 0 && index < ClientPrizmodState.BOX.size()) ? ClientPrizmodState.BOX.get(index) : null;
            case TYPE_TEAM -> (index >= 0 && index < ClientPrizmodState.TEAM.size()) ? ClientPrizmodState.TEAM.get(index) : null;
            case TYPE_BABY -> ClientPrizmodState.BABY;
            default -> null;
        };
    }

    private int hitBoxIndex(double mx, double my) {
        for (int r = 0; r < BOX_ROWS; r++) {
            for (int c = 0; c < BOX_COLS; c++) {
                int sx = invX0 + c * (SLOT_W + PAD_X);
                int sy = invY0 + r * (SLOT_H + PAD_Y);
                if (isInside(mx, my, sx, sy)) return r * BOX_COLS + c;
            }
        }
        return -1;
    }

    private int hitTeamIndex(double mx, double my) {
        for (int r = 0; r < TEAM_ROWS; r++) {
            for (int c = 0; c < TEAM_COLS; c++) {
                int sx = teamX0 + c * (SLOT_W + PAD_X);
                int sy = teamY0 + r * (SLOT_H + PAD_Y);
                if (isInside(mx, my, sx, sy)) return r * TEAM_COLS + c;
            }
        }
        return -1;
    }

    private boolean hitBaby(double mx, double my) {
        return isInside(mx, my, babyX, babyY);
    }

    private int[] hitAny(double mx, double my) {
        int bi = hitBoxIndex(mx, my);
        if (bi != -1) return new int[]{TYPE_BOX, bi};

        int ti = hitTeamIndex(mx, my);
        if (ti != -1) return new int[]{TYPE_TEAM, ti};

        if (hitBaby(mx, my)) return new int[]{TYPE_BABY, 0};

        return new int[]{TYPE_NONE, -1};
    }

    private boolean isChild(ClientPrizmodState.Entry e) {
        return e != null && "CHILD".equalsIgnoreCase(e.stage());
    }

    private int firstEmptyTeamSlot() {
        for (int i = 0; i < 6; i++) {
            if (i >= ClientPrizmodState.TEAM.size() || ClientPrizmodState.TEAM.get(i) == null) return i;
        }
        return -1;
    }

    private boolean babySlotEmpty() {
        return ClientPrizmodState.BABY == null;
    }

    private void quickMove(int fromType, int fromIndex, ClientPrizmodState.Entry e) {
        if (e == null) return;

        int toType = TYPE_NONE;
        int toIndex = -1;

        if (isChild(e)) {
            // CHILD -> baby slot
            if (fromType == TYPE_BABY) {
                // del baby slot a la box (al final)
                toType = TYPE_BOX;
                toIndex = 9999;
            } else {
                if (babySlotEmpty()) {
                    toType = TYPE_BABY;
                    toIndex = 0;
                } else {
                    // baby slot ocupado -> a la box (al final)
                    toType = TYPE_BOX;
                    toIndex = 9999;
                }
            }
        } else {
            // ADULT/EVOLVED -> team
            if (fromType == TYPE_BOX) {
                int slot = firstEmptyTeamSlot();
                if (slot != -1) {
                    toType = TYPE_TEAM;
                    toIndex = slot;
                } else {
                    return;
                }
            } else if (fromType == TYPE_TEAM) {
                toType = TYPE_BOX;
                toIndex = 9999;
            } else {
                // si alguien intentase quickmove desde baby (no debería pasar con isChild)
                toType = TYPE_BOX;
                toIndex = 9999;
            }
        }

        PacketDistributor.sendToServer(new C2SMoveSpectrobe(fromType, fromIndex, toType, toIndex));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button == 1 && dragging) {
            dragging = false;
            holdingMouseDrag = false;
            dragFromType = -1;
            dragFromIndex = -1;
            dragEntry = null;
            return true;
        }

        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int[] hit = hitAny(mouseX, mouseY);
        int type = hit[0];
        int index = hit[1];

        if (type == TYPE_NONE) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (hasShiftDown() && !dragging) {
            ClientPrizmodState.Entry e = getEntry(type, index);
            quickMove(type, index, e);
            return true;
        }

        if (!dragging) {
            ClientPrizmodState.Entry e = getEntry(type, index);
            if (e != null) {
                dragging = true;
                holdingMouseDrag = true;
                dragFromType = type;
                dragFromIndex = index;
                dragEntry = e;
            }
            return true;
        }

        if (dragFromType == type && dragFromIndex == index) {
            holdingMouseDrag = false;
            return true;
        }

        PacketDistributor.sendToServer(new C2SMoveSpectrobe(dragFromType, dragFromIndex, type, index));

        dragging = false;
        holdingMouseDrag = false;
        dragFromType = -1;
        dragFromIndex = -1;
        dragEntry = null;

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging && holdingMouseDrag) {

            int[] hit = hitAny(mouseX, mouseY);
            int toType = hit[0];
            int toIndex = hit[1];

            if (toType != TYPE_NONE && !(toType == dragFromType && toIndex == dragFromIndex)) {
                PacketDistributor.sendToServer(new C2SMoveSpectrobe(dragFromType, dragFromIndex, toType, toIndex));

                dragging = false;
                dragFromType = -1;
                dragFromIndex = -1;
                dragEntry = null;
            }

            holdingMouseDrag = false;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private List<Component> buildEntryTooltip(ClientPrizmodState.Entry e) {
        List<Component> lines = new ArrayList<>();

        String key = e.species() == null ? "unknown" : e.species().toLowerCase();
        lines.add(Component.translatable("spectrobesreforged.spectrobe." + key)
                .withStyle(ChatFormatting.AQUA));

        lines.add(Component.literal("Stage: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(e.stage()).withStyle(ChatFormatting.WHITE)));

        lines.add(Component.literal("Level: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(e.level())).withStyle(ChatFormatting.WHITE)));

        lines.add(Component.literal("Variant: ").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal(String.valueOf(e.color())).withStyle(ChatFormatting.GRAY)));

        int total = e.hp() + e.atk() + e.def();

        lines.add(Component.literal("HP: ").withStyle(ChatFormatting.RED)
                .append(Component.literal(String.valueOf(e.hp())).withStyle(ChatFormatting.WHITE)));

        lines.add(Component.literal("ATK: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(String.valueOf(e.atk())).withStyle(ChatFormatting.WHITE)));

        lines.add(Component.literal("DEF: ").withStyle(ChatFormatting.BLUE)
                .append(Component.literal(String.valueOf(e.def())).withStyle(ChatFormatting.WHITE)));

        lines.add(Component.literal("Total: ").withStyle(ChatFormatting.DARK_AQUA)
                .append(Component.literal(String.valueOf(total)).withStyle(ChatFormatting.AQUA)));

        return lines;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);

        if (dragging && dragEntry != null) {
            String species = dragEntry.species();

            if (species != null && species.equalsIgnoreCase("komainu")) {
                var tex = SpectrobeIcons.icon(species, dragEntry.color());
                g.blit(tex, mouseX - 8, mouseY - 8, 0, 0, 16, 16, 16, 16);
            } else if (species != null) {
                String label = species.toLowerCase();
                if (label.length() > 6) label = label.substring(0, 6);
                g.drawString(font, label, mouseX - 10, mouseY - 4, 0xFFFFFF);
            }
        }

        // Custom tooltip for spectrobe icons (our custom slots)
        if (!dragging) {
            int[] hit = hitAny(mouseX, mouseY);
            int type = hit[0];
            int index = hit[1];

            if (type != TYPE_NONE) {
                ClientPrizmodState.Entry hovered = getEntry(type, index);
                if (hovered != null) {
                    g.renderTooltip(this.font, buildEntryTooltip(hovered), Optional.empty(), mouseX, mouseY);
                    return; // evitamos tooltips vanilla
                }
            }
        }

        this.renderTooltip(g, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        // no vanilla labels
    }
}