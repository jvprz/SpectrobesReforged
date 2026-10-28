package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeType;
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

    @Override public Component getName(ItemStack stack) {
        var name=Component.empty().append(super.getName(stack));
        if(RawFossilItem.data(stack).getBoolean("ExcavationCrown"))name.append(Component.literal(" ♛").withStyle(ChatFormatting.GOLD));
        if(RawFossilItem.data(stack).getBoolean("ExcavationCooperative"))name.append(Component.literal(" ⚒").withStyle(ChatFormatting.AQUA));
        return name;
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if(RawFossilItem.data(stack).getBoolean("ExcavationCooperative"))tooltip.add(Component.translatable("tooltip.spectrobesreforged.excavation_cooperative").withStyle(ChatFormatting.AQUA));
        var excavation = RawFossilItem.data(stack);
        if (excavation.getBoolean("ExcavationCrown")) tooltip.add(Component.translatable("tooltip.spectrobesreforged.excavation_crown").withStyle(ChatFormatting.GOLD));
        if (excavation.contains("ExcavationIntegrity")) tooltip.add(Component.translatable("tooltip.spectrobesreforged.excavation_quality",
                excavation.getInt("ExcavationIntegrity"), excavation.getInt("ExcavationSeconds")));
        if(excavation.contains("ExcavationIntegrity"))tooltip.add(Component.translatable("tooltip.spectrobesreforged.quality_bonus",
                com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.qualityPercent(excavation.getInt("ExcavationIntegrity"))).withStyle(ChatFormatting.AQUA));
        // Línea vacía para el icono (tooltip image)
        tooltip.add(Component.empty());

        // Línea de texto para futuras stats: "Type: Aurora"
        tooltip.add(Component.translatable("tooltip.spectrobesreforged.type_line",
                Component.translatable(typeKey(type)).withStyle(typeColor(type))
        ));
    }

    private static String typeKey(SpectrobeType t) {
        return switch (t) {
            case AURORA -> "tooltip.spectrobesreforged.type.aurora";
            case FLASH  -> "tooltip.spectrobesreforged.type.flash";
            case CORONA -> "tooltip.spectrobesreforged.type.corona";
        };
    }

    private static ChatFormatting typeColor(SpectrobeType t) {
        return switch (t) {
            case AURORA -> ChatFormatting.GREEN;
            case FLASH  -> ChatFormatting.AQUA;
            case CORONA -> ChatFormatting.RED;
        };
    }
}