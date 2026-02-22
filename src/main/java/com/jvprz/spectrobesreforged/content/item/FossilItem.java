package com.jvprz.spectrobesreforged.content.item;

import com.jvprz.spectrobesreforged.content.SpectrobeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class FossilItem extends Item {
    private final SpectrobeType type;

    public FossilItem(SpectrobeType type, Properties props) {
        super(props);
        this.type = type;
    }

    public SpectrobeType getType() {
        return type;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(typeLine(type));
    }

    private static Component typeLine(SpectrobeType t) {
        return switch (t) {
            case AURORA -> Component.translatable("tooltip.spectrobesreforged.type.aurora").withStyle(ChatFormatting.GREEN);
            case FLASH  -> Component.translatable("tooltip.spectrobesreforged.type.flash").withStyle(ChatFormatting.AQUA);
            case CORONA -> Component.translatable("tooltip.spectrobesreforged.type.corona").withStyle(ChatFormatting.RED);
        };
    }
}