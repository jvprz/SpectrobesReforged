package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import java.util.List;

public final class RawFossilItem extends Item {
    private final SpectrobeType element;
    public RawFossilItem(SpectrobeType element, Properties properties) { super(properties.stacksTo(1)); this.element = element; }
    public SpectrobeType element() { return element; }
    public List<String> candidates() {
        var loaded=com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry.all().stream()
                .filter(species->species.type()==element&&species.initialStage()==com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeStage.CHILD)
                .map(com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeSpecies::key)
                .filter(key->com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationMasks.get(key)!=null)
                .sorted().toList();
        if(!loaded.isEmpty())return loaded;
        return switch(element){case CORONA->List.of("komainu","inkana","vilar");case AURORA->List.of("segu","spiko");case FLASH->List.of("shakin");};
    }
    public static CompoundTag data(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.spectrobesreforged.raw_fossil"));
        if (data(stack).contains("Excavation")) lines.add(Component.translatable("tooltip.spectrobesreforged.excavation_saved"));
    }
}
