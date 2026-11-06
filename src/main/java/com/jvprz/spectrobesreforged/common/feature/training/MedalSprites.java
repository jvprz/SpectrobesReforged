package com.jvprz.spectrobesreforged.common.feature.training;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

/** Bitmap-backed text components work in item names, labels and interfaces on both sides. */
public final class MedalSprites {
    private MedalSprites() {}
    public static final ResourceLocation FONT=ResourceLocation.fromNamespaceAndPath("spectrobesreforged","medals");
    public static MutableComponent perfect() {return glyph("\uE000");}
    public static MutableComponent cooperative() {return glyph("\uE001");}
    public static MutableComponent withoutMedals(Component text) {
        if(FONT.equals(text.getStyle().getFont()))return Component.empty();
        var copy=MutableComponent.create(text.getContents()).setStyle(text.getStyle());
        for(var sibling:text.getSiblings())copy.append(withoutMedals(sibling));
        return copy;
    }
    private static MutableComponent glyph(String value) {
        return Component.literal(value).withStyle(Style.EMPTY.withFont(FONT).withColor(0xffffff).withBold(false).withItalic(false));
    }
}
