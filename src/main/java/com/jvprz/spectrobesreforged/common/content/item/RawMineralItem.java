package com.jvprz.spectrobesreforged.common.content.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import java.util.List;

public final class RawMineralItem extends Item {
    private final String grade;
    public RawMineralItem(String grade) { this(grade,new Properties()); }
    public RawMineralItem(String grade,Properties properties) { super(properties.stacksTo(1)); this.grade = grade; }
    public List<String> candidates() { return List.of("power_" + grade, "defense_" + grade, "health_" + grade); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.spectrobesreforged.raw_mineral"));
        if (RawFossilItem.data(stack).contains("Excavation")) lines.add(Component.translatable("tooltip.spectrobesreforged.excavation_saved"));
    }
}
