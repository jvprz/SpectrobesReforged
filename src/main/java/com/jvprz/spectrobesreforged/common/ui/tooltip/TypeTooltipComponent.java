package com.jvprz.spectrobesreforged.common.ui.tooltip;

import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record TypeTooltipComponent(SpectrobeType type) implements TooltipComponent {}