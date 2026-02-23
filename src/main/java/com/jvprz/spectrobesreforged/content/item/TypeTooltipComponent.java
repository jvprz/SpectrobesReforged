package com.jvprz.spectrobesreforged.content.item;

import com.jvprz.spectrobesreforged.content.SpectrobeType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record TypeTooltipComponent(SpectrobeType type) implements TooltipComponent {}