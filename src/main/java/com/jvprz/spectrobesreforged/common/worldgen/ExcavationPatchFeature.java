package com.jvprz.spectrobesreforged.common.worldgen;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import java.util.ArrayList;

/** Buried geological pocket with one central deposit, restricted to its own biome family. */
public final class ExcavationPatchFeature extends Feature<ExcavationPatchFeature.Config> {
 public record Config(BlockState host,BlockState ore,ResourceLocation biomeTag) implements FeatureConfiguration {
  public static final Codec<Config> CODEC=RecordCodecBuilder.create(i->i.group(
   BlockState.CODEC.fieldOf("host").forGetter(Config::host),
   BlockState.CODEC.fieldOf("ore").forGetter(Config::ore),
   ResourceLocation.CODEC.fieldOf("biome_tag").forGetter(Config::biomeTag)
  ).apply(i,Config::new));
 }
 private static final TagKey<Block> REPLACEABLE=TagKey.create(Registries.BLOCK,ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"excavation_patch_replaceable"));
 public ExcavationPatchFeature(){super(Config.CODEC);}
 @Override public boolean place(FeaturePlaceContext<Config> context){
  var level=context.level();var random=context.random();var config=context.config();
  var centre=context.origin().below(2+random.nextInt(5));
  var biomes=TagKey.create(Registries.BIOME,config.biomeTag());
  if(!level.getBiome(centre).is(biomes))return false;
  // Validate all of the central shell before writing anything: no floating/cave-wall fossils.
  for(var direction:Direction.values()){
   var pos=centre.relative(direction);
   if(!level.ensureCanWrite(pos)||!level.getBiome(pos).is(biomes)||!level.getBlockState(pos).is(REPLACEABLE)||level.getBlockState(pos).hasBlockEntity())return false;
  }
  if(!level.ensureCanWrite(centre)||!level.getBlockState(centre).is(REPLACEABLE)||level.getBlockState(centre).hasBlockEntity())return false;
  var positions=new ArrayList<BlockPos>();
  for(var offset:ExcavationPatchShape.cells(2+random.nextInt(2),1+random.nextInt(2))){
   var pos=centre.offset(offset.x(),offset.y(),offset.z());
   if(!level.ensureCanWrite(pos))return false;
   var old=level.getBlockState(pos);
   if(level.getBiome(pos).is(biomes)&&old.is(REPLACEABLE)&&!old.hasBlockEntity())positions.add(pos);
  }
  // Existing ores, structures, fluids, logs and air are never replacement targets.
  for(var pos:positions)if(!pos.equals(centre))setBlock(level,pos,config.host());
  setBlock(level,centre,config.ore());
  return true;
 }
}
