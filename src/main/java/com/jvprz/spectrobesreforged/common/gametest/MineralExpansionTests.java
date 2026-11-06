package com.jvprz.spectrobesreforged.common.gametest;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.item.*;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.*;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.registry.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.*;
import java.util.*;

@GameTestHolder(SpectrobesReforged.MODID)
@PrefixGameTestTemplate(false)
public final class MineralExpansionTests {
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void depositsKeepSurfaceCoverAndChildRecognizesEveryOre(GameTestHelper h){
        var level=h.getLevel();
        for(int x=2;x<=6;x++)for(int z=2;z<=6;z++)for(int y=1;y<=8;y++)h.setBlock(new net.minecraft.core.BlockPos(x,y,z),net.minecraft.world.level.block.Blocks.STONE);
        var centre=h.absolutePos(new net.minecraft.core.BlockPos(4,4,4));
        h.assertTrue(com.jvprz.spectrobesreforged.common.worldgen.ExcavationPatchFeature.buried(level,centre),"Closed underground cell can host a deposit");
        h.assertFalse(com.jvprz.spectrobesreforged.common.worldgen.ExcavationPatchFeature.buried(level,h.absolutePos(new net.minecraft.core.BlockPos(4,8,4))),"Surface cell must never become an exposed patch");
        var cave=centre.north();level.setBlock(cave,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),3);
        h.assertFalse(com.jvprz.spectrobesreforged.common.worldgen.ExcavationPatchFeature.buried(level,centre),"Cave wall cannot expose a patch");
        level.setBlock(cave,net.minecraft.world.level.block.Blocks.WATER.defaultBlockState(),3);
        h.assertFalse(com.jvprz.spectrobesreforged.common.worldgen.ExcavationPatchFeature.buried(level,centre),"Water boundary cannot expose a patch");
        level.setBlock(cave,net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),3);
        for(var block:ModBlocks.ELEMENTAL_MINERAL_ORES.values())h.assertTrue(com.jvprz.spectrobesreforged.common.feature.scanner.ScanTargets.kind(block.get().defaultBlockState())==com.jvprz.spectrobesreforged.common.feature.scanner.ScanTargets.Kind.MINERAL,"Child recognizes every elemental host");
        for(var d:com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationDeposits.all()){
            var expected=d.fossil()?com.jvprz.spectrobesreforged.common.feature.scanner.ScanTargets.Kind.FOSSIL:com.jvprz.spectrobesreforged.common.feature.scanner.ScanTargets.Kind.MINERAL;
            h.assertTrue(com.jvprz.spectrobesreforged.common.feature.scanner.ScanTargets.kind(ModBlocks.ENVIRONMENTAL_ORES.get(d.id()).get().defaultBlockState())==expected,"Child recognizes environmental and legacy deposits");
        }
        h.assertTrue(com.jvprz.spectrobesreforged.common.feature.scanner.ScanTargets.kind(net.minecraft.world.level.block.Blocks.BLUE_ICE.defaultBlockState())==null,"Plain host blocks do not produce false scanner signals");
        h.succeed();
    }
    @GameTest(template="server_smoke",timeoutTicks=100)
    public static void elementalOresKeepGradeFamilyGeologyAndSilkTouch(GameTestHelper h){
        var pos=h.absolutePos(new net.minecraft.core.BlockPos(1,1,1));
        var iron=net.minecraft.world.item.Items.IRON_PICKAXE.getDefaultInstance();
        var silk=net.minecraft.world.item.Items.DIAMOND_PICKAXE.getDefaultInstance();
        silk.enchant(h.getLevel().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH),1);
        h.assertTrue(ModBlocks.ELEMENTAL_MINERAL_ORES.size()==10,"Ten visible elemental ore variants");
        for(var entry:ModBlocks.ELEMENTAL_MINERAL_ORES.entrySet())for(int grade=0;grade<4;grade++){
            var ore=(com.jvprz.spectrobesreforged.common.content.block.ElementalMineralOreBlock)entry.getValue().get();
            var state=ore.defaultBlockState().setValue(com.jvprz.spectrobesreforged.common.content.block.ElementalMineralOreBlock.GRADE,grade);
            h.assertTrue(iron.isCorrectToolForDrops(state),"New ore remains mineable with iron: "+entry.getKey());
            var drops=net.minecraft.world.level.block.Block.getDrops(state,h.getLevel(),pos,null,null,iron);
            h.assertTrue(drops.size()==1,"Exactly one specimen per ore");
            var raw=drops.getFirst();String expected=ore.family()+"_"+List.of("c","b","a","a_plus").get(grade);
            h.assertTrue(RawFossilItem.data(raw).getString("Mineral").equals(expected),"Ore loot preserves hidden grade: "+expected);
            h.assertTrue(((RawMineralItem)raw.getItem()).candidates(raw).contains(expected),"Excavation accepts fixed result");
            var entity=new net.minecraft.world.entity.item.ItemEntity(h.getLevel(),pos.getX(),pos.getY(),pos.getZ(),raw);
            var event=new net.neoforged.neoforge.event.level.BlockDropsEvent(h.getLevel(),pos,state,null,new ArrayList<>(List.of(entity)),null,iron);
            com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationTerrainEvents.drops(event);
            h.assertTrue(RawFossilItem.data(entity.getItem()).getString("MineralFamily").equals(ore.family()),"Block family wins over placement biome");
            var silkDrops=net.minecraft.world.level.block.Block.getDrops(state,h.getLevel(),pos,null,null,silk);
            h.assertTrue(silkDrops.size()==1&&silkDrops.getFirst().is(ore.asItem()),"Silk touch returns matching ore");
            h.assertTrue(silkDrops.getFirst().get(net.minecraft.core.component.DataComponents.BLOCK_STATE).properties().get("grade").equals(Integer.toString(grade)),"Silk touch keeps geological grade on replacement");
        }
        h.succeed();
    }
    @GameTest(template="server_smoke",timeoutTicks=100)
    public static void retiredMineralsLoadAsCanonicalItemsWithoutLosingStacks(GameTestHelper h){
        for(var alias:Map.of("uranium","aurorium_s","chrome","flashium_s","evolve","evolve_m","titanium_plate","titanium").entrySet()){
            var tag=new net.minecraft.nbt.CompoundTag();
            tag.putString("id","spectrobesreforged:"+alias.getKey());tag.putInt("count",3);
            var stack=ItemStack.parse(h.getLevel().registryAccess(),tag).orElseThrow();
            var expected=net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,alias.getValue()));
            h.assertTrue(stack.is(expected)&&stack.getCount()==3,"Old saved stack must migrate without loss: "+alias.getKey());
            h.assertTrue(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().equals(alias.getValue()),"No duplicate canonical item");
        }
        var titanium=ModMineralLoot.MATERIALS.get("titanium").get();
        h.assertTrue(titanium instanceof PreparedLootItem&&!(titanium instanceof MineralItem),"Titanium is material, not food");
        h.assertFalse(titanium.getDefaultInstance().is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"minerals"))),"Titanium must not attract a hungry companion");
        h.succeed();
    }
    @GameTest(template="server_smoke",timeoutTicks=100)
    public static void miningFixesFamilyAndPortableSpecimensKeepIt(GameTestHelper h){
        var pos=h.absolutePos(new net.minecraft.core.BlockPos(2,1,2));
        var raw=ModItems.RAW_C_MINERAL.get().getDefaultInstance();
        net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,raw,tag->tag.putString("Mineral","power_c"));
        var drop=new net.minecraft.world.entity.item.ItemEntity(h.getLevel(),pos.getX(),pos.getY(),pos.getZ(),raw);
        var event=new net.neoforged.neoforge.event.level.BlockDropsEvent(h.getLevel(),pos,ModBlocks.C_MINERAL_ORE.get().defaultBlockState(),null,
                new java.util.ArrayList<>(List.of(drop)),null,net.minecraft.world.item.Items.DIAMOND_PICKAXE.getDefaultInstance());
        com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationTerrainEvents.drops(event);
        var saved=RawFossilItem.data(raw);String family=saved.getString("MineralFamily");
        h.assertTrue(List.of("coronium","aurorium","flashium").contains(family),"Mining must fix a real elemental family");
        h.assertTrue(saved.getString("Mineral").equals(family+"_c"),"Legacy loot result replaced by source-biome element");
        var copy=raw.copy();h.assertTrue(((RawMineralItem)copy.getItem()).candidates(copy).equals(List.of(family+"_c")),"Portable copies retain the result independently of table location");
        h.succeed();
    }
    @GameTest(template="server_smoke",timeoutTicks=100)
    public static void feedingConsumesOnlySuccessfulMineralsAndPreservesSeedLimit(GameTestHelper h){
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"MineralTester"));p.setGameMode(GameType.SURVIVAL);
        var data=p.getData(ModAttachments.PRIZMOD.get());var species=SpectrobeSpeciesRegistry.getByKey("komainu");
        var e=new SpectrobeEntry(UUID.randomUUID(),"komainu",0,"CHILD",1,species.stats().hp().base(),species.stats().hp().base(),species.stats().attack().base(),species.stats().defense().base(),0,0,0,0);
        data.addToBox(e);
        var seed=ModMineralLoot.SEEDS.get("atk").get();var stack=new ItemStack(seed,2);
        h.assertTrue(((MineralItem)seed).feedTo(p,e.id(),stack,null),"First seed must train the entry");
        h.assertTrue(stack.getCount()==1,"One seed consumed");
        var after=data.findAnywhere(e.id()).orElseThrow();
        h.assertTrue(after.progress().seeds()==1&&after.xp()==0,"Seed allowance persisted, no XP awarded");
        h.assertFalse(((MineralItem)seed).feedTo(p,e.id(),stack,null),"Second seed blocked at level one");
        h.assertTrue(stack.getCount()==1,"Rejected seed must not be consumed");
        var elemental=ModMineralLoot.ELEMENTALS.get("coronium_c").get();var food=new ItemStack(elemental);
        h.assertTrue(((MineralItem)elemental).feedTo(p,e.id(),food,null),"Elemental mineral gives XP");
        h.assertTrue(food.isEmpty()&&data.findAnywhere(e.id()).orElseThrow().xp()>0,"Successful elemental feed consumes exactly one");
        var raw=ModItems.RAW_C_MINERAL.get().getDefaultInstance();
        net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,raw,tag->tag.putString("MineralFamily","flashium"));
        h.assertTrue(((RawMineralItem)raw.getItem()).candidates(raw).equals(List.of("flashium_c")),"Saved source biome determines the result");
        h.succeed();
    }
    @GameTest(template="server_smoke",timeoutTicks=100)
    public static void preparedLootIsRegisteredWithoutOpeningRewards(GameTestHelper h){
        h.assertTrue(ModItems.RAW_ELEMENTAL_MINERALS.size()==7,"Only seven current portable raw variants");
        for(var item:ModItems.RAW_ELEMENTAL_MINERALS.values())for(String grade:List.of("c","b","a","a_plus","s")){
            var raw=item.get().getDefaultInstance();
            String family=com.jvprz.spectrobesreforged.common.content.item.RawFossilItem.data(raw).getString("MineralFamily");
            String result=family+"_"+grade;
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,raw,tag->tag.putString("Mineral",result));
            h.assertTrue(((RawMineralItem)raw.getItem()).candidates(raw).contains(result),"Unified raw preserves every saved clean grade: "+result);
        }
        h.assertTrue(ModMineralLoot.ELEMENTALS.size()==15&&ModMineralLoot.SEEDS.size()==3&&ModMineralLoot.EVOLVE.size()==3,"All mineral families registered");
        h.assertTrue(ModMineralLoot.LOOT.size()==30&&ModMineralLoot.MATERIALS.size()==3,"20 cubes, two relics, eight mysteries and three materials");
        for(var item:ModMineralLoot.LOOT.values())h.assertTrue(item.get() instanceof PreparedLootItem,"Prepared objects have no loot-opening behavior");
        h.succeed();
    }
}
