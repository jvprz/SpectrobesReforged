package com.jvprz.spectrobesreforged.common.feature.excavation;

import com.jvprz.spectrobesreforged.common.content.item.*;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public final class ExcavationSpecimen {
    private ExcavationSpecimen() {}
    public static void appendGeology(ItemStack stack, java.util.List<net.minecraft.network.chat.Component> lines) {
        var tag=RawFossilItem.data(stack);
        if(!tag.contains("ExcavationTerrain")&&!tag.contains("Excavation"))return;
        var saved=tag.getCompound("Excavation");
        int terrain=Math.clamp(saved.contains("Terrain")?saved.getInt("Terrain"):tag.getInt("ExcavationTerrain"),0,5);
        int rock=Math.clamp(saved.contains("Rock")?saved.getInt("Rock"):tag.getInt("ExcavationRock"),0,4);
        lines.add(net.minecraft.network.chat.Component.translatable("tooltip.spectrobesreforged.geology",
                net.minecraft.network.chat.Component.translatable("tooltip.spectrobesreforged.layer_"+terrain),
                net.minecraft.network.chat.Component.translatable("tooltip.spectrobesreforged.rock_"+rock)));
    }
    public static boolean accepts(ItemStack stack) { return stack.getItem() instanceof RawFossilItem || stack.getItem() instanceof RawMineralItem; }
    public static List<String> candidates(ItemStack stack) {
        if (stack.getItem() instanceof RawFossilItem fossil) return fossil.candidates();
        if (stack.getItem() instanceof RawMineralItem mineral) return mineral.candidates();
        return List.of();
    }
}
