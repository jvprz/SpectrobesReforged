package com.jvprz.spectrobesreforged.common.feature.excavation;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.item.RawFossilItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/** Preserve the dig site's environment on the portable specimen, not the table's biome. */
@EventBusSubscriber(modid = SpectrobesReforged.MODID)
public final class ExcavationTerrainEvents {
    private ExcavationTerrainEvents() {}
    public static int terrain(Level level, BlockPos pos) {
        for (var direction : net.minecraft.core.Direction.values()) {
            var fluid = level.getFluidState(pos.relative(direction));
            if (fluid.is(FluidTags.LAVA)) return ExcavationPuzzle.LAVA;
        }
        var biome = level.getBiome(pos);
        if (biome.value().coldEnoughToSnow(pos)) return ExcavationPuzzle.ICE;
        if (biome.is(Biomes.SWAMP) || biome.is(Biomes.MANGROVE_SWAMP)) return ExcavationPuzzle.MUD;
        if (biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_RIVER) || level.getFluidState(pos.above()).is(FluidTags.WATER)) return ExcavationPuzzle.WATER_TERRAIN;
        if (biome.is(Biomes.DESERT) || biome.is(BiomeTags.IS_BADLANDS) || biome.is(BiomeTags.IS_BEACH)) return ExcavationPuzzle.SAND;
        return ExcavationPuzzle.ROCK;
    }
    @SubscribeEvent public static void drops(BlockDropsEvent event) {
        for (var entity : event.getDrops()) {
            var stack = entity.getItem();
            // Table contents retain their previous environment and progress when the table breaks.
            if (!ExcavationSpecimen.accepts(stack) || RawFossilItem.data(stack).contains("Excavation")) continue;
            String block=BuiltInRegistries.BLOCK.getKey(event.getState().getBlock()).getPath();
            var deposit=ExcavationDeposits.find(block);
            int environment=deposit.map(ExcavationDeposits.Deposit::terrain).orElseGet(()->terrain(event.getLevel(),event.getPos()));
            int rock=deposit.map(ExcavationDeposits.Deposit::rock).orElse(block.startsWith("deepslate_")?ExcavationProfile.DEEPSLATE:ExcavationProfile.STONE);
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
                if(!tag.contains("ExcavationTerrain"))tag.putInt("ExcavationTerrain",environment);
                if(!tag.contains("ExcavationRock"))tag.putInt("ExcavationRock",rock);
            });
        }
    }
}
