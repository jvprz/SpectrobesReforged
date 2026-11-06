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
            int originalEnvironment=deposit.map(ExcavationDeposits.Deposit::terrain).orElseGet(()->terrain(event.getLevel(),event.getPos()));
            int originalRock=deposit.map(ExcavationDeposits.Deposit::rock).orElse(block.startsWith("deepslate_")?ExcavationProfile.DEEPSLATE:ExcavationProfile.STONE);
            String climateFamily=switch(com.jvprz.spectrobesreforged.common.feature.vortex.VortexRules.element(
                    com.jvprz.spectrobesreforged.common.feature.vortex.VortexClimate.krawl(event.getLevel().getBiome(event.getPos())))) {
                case "corona"->"coronium";case "aurora"->"aurorium";default->"flashium";
            };
            var elemental=event.getState().getBlock() instanceof com.jvprz.spectrobesreforged.common.content.block.ElementalMineralOreBlock ore?ore:null;
            String host=elemental!=null?elemental.host():deposit.map(ExcavationDeposits.Deposit::host).orElse("");
            String family=switch(host){case "frozen"->"flashium";case "muddy"->"aurorium";case "sandy","red_sandy"->"coronium";default->elemental!=null?elemental.family():climateFamily;};
            int environment=switch(host){case "frozen"->ExcavationPuzzle.ICE;case "muddy"->ExcavationPuzzle.MUD;case "sandy","red_sandy"->ExcavationPuzzle.SAND;default->originalEnvironment;};
            int rock=switch(host){case "deepslate"->ExcavationProfile.DEEPSLATE;case "sandy"->ExcavationProfile.SANDSTONE;case "red_sandy"->ExcavationProfile.RED_SANDSTONE;case "muddy"->ExcavationProfile.CLAY;default->originalRock;};
            String grade=stack.getItem() instanceof com.jvprz.spectrobesreforged.common.content.item.RawMineralItem raw?raw.grade(stack):"";
            String resultGrade=grade.equals("a_plus")&&event.getLevel().random.nextInt(100)==0?"s":grade;
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
                if(!grade.isEmpty()){
                    tag.putString("MineralFamily",family);
                    tag.putString("Mineral",family+"_"+resultGrade);
                }
                if(!tag.contains("ExcavationTerrain"))tag.putInt("ExcavationTerrain",environment);
                if(!tag.contains("ExcavationRock"))tag.putInt("ExcavationRock",rock);
            });
            if(!grade.isEmpty()){
                String prefix=switch(host){case "frozen"->"ice_";case "muddy"->"mud_";case "sandy"->"sand_";case "red_sandy"->"red_sand_";default->"";};
                var replacement=new net.minecraft.world.item.ItemStack(com.jvprz.spectrobesreforged.common.registry.ModItems.RAW_ELEMENTAL_MINERALS.get(prefix+"raw_"+family+"_mineral").get(),stack.getCount());
                replacement.applyComponents(stack.getComponentsPatch());
                entity.setItem(replacement);
            }
        }
    }
}
