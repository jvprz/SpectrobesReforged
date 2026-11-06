package com.jvprz.spectrobesreforged.common.worldgen;

import com.jvprz.spectrobesreforged.common.registry.ModBlocks;
import com.jvprz.spectrobesreforged.common.content.block.ElementalMineralOreBlock;
import com.jvprz.spectrobesreforged.common.feature.vortex.VortexClimate;
import com.jvprz.spectrobesreforged.common.feature.vortex.VortexRules;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

/** Reuses vanilla veins and the existing per-grade placement rates, including modded biome climates. */
public final class ElementalMineralOreFeature extends Feature<OreConfiguration> {
    public ElementalMineralOreFeature(){super(OreConfiguration.CODEC);}
    @Override public boolean place(FeaturePlaceContext<OreConfiguration> context){
        String family=switch(VortexRules.element(VortexClimate.krawl(context.level().getBiome(context.origin())))){
            case "corona"->"coronium";case "aurora"->"aurorium";default->"flashium";};
        var targets=context.config().targetStates.stream().map(target->{
            String id=BuiltInRegistries.BLOCK.getKey(target.state.getBlock()).getPath();
            int grade=id.contains("a_plus_")?3:id.contains("a_mineral")?2:id.contains("b_mineral")?1:0;
            String prefix=id.startsWith("deepslate_")?"deepslate_":"";
            return OreConfiguration.target(target.target,ModBlocks.ELEMENTAL_MINERAL_ORES.get(prefix+family+"_ore").get().defaultBlockState().setValue(ElementalMineralOreBlock.GRADE,grade));
        }).toList();
        var config=new OreConfiguration(targets,context.config().size,context.config().discardChanceOnAirExposure);
        return Feature.ORE.place(config,context.level(),context.chunkGenerator(),context.random(),context.origin());
    }
}
