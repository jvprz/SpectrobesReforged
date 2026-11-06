package com.jvprz.spectrobesreforged.common.gametest;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.item.*;
import com.jvprz.spectrobesreforged.common.feature.fieldtools.*;
import com.jvprz.spectrobesreforged.common.registry.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.gametest.*;
import java.util.*;

@GameTestHolder(SpectrobesReforged.MODID)
@PrefixGameTestTemplate(false)
public final class FieldToolTests {
    private static ServerPlayer player(GameTestHelper h,String name){
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),name));p.setGameMode(GameType.SURVIVAL);p.setHealth(20);
        var pos=h.absolutePos(new BlockPos(4,1,1));p.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,0,0);return p;
    }
    public static final class Protection {
        private final UUID player;private final BlockPos pos;private final boolean placement;
        Protection(ServerPlayer player,BlockPos pos,boolean placement){this.player=player.getUUID();this.pos=pos;this.placement=placement;}
        @SubscribeEvent public void broken(BlockEvent.BreakEvent event){if(!placement&&event.getPlayer().getUUID().equals(player)&&event.getPos().equals(pos))event.setCanceled(true);}
        @SubscribeEvent public void placed(BlockEvent.EntityPlaceEvent event){if(placement&&event.getEntity()!=null&&event.getEntity().getUUID().equals(player)&&event.getPos().equals(pos))event.setCanceled(true);}
    }
    @GameTest(template="server_smoke",timeoutTicks=100)
    public static void allPortableToolsAreDurableAndHaveLoadedRecipes(GameTestHelper h){
        h.assertTrue(ModItems.EXCAVATION_TOOLS.size()==10,"Ten portable tools");
        for(var entry:ModItems.EXCAVATION_TOOLS.entrySet()){
            var stack=entry.getValue().get().getDefaultInstance();
            h.assertTrue(stack.isDamageableItem()&&stack.getCount()==1,"Tool is durable and unstackable: "+entry.getKey());
            var id=net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"excavation_"+entry.getKey());
            h.assertTrue(h.getLevel().getRecipeManager().byKey(id).isPresent(),"Crafting recipe loaded: "+id);
        }
        h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void recoveryHealsWithoutBypassingCooldown(GameTestHelper h){
        var p=player(h,"RecoveryTest");p.setHealth(10);var stack=new ItemStack(ModItems.EXCAVATION_TOOLS.get("recovery").get());p.setItemInHand(InteractionHand.MAIN_HAND,stack);
        stack.getItem().finishUsingItem(stack,h.getLevel(),p);
        h.assertTrue(p.getHealth()==14&&stack.getDamageValue()==1,"Recovery restores two hearts and spends one use");
        stack.getItem().finishUsingItem(stack,h.getLevel(),p);
        h.assertTrue(p.getHealth()==14&&stack.getDamageValue()==1,"Cooldown blocks repeated healing");h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void thermalToolsMeltAndCoolWithoutCleaningDeposits(GameTestHelper h){
        var p=player(h,"ThermalTools");var fire=new ItemStack(ModItems.EXCAVATION_TOOLS.get("fire").get());p.setItemInHand(InteractionHand.MAIN_HAND,fire);
        var pos=h.absolutePos(new BlockPos(4,2,4));h.getLevel().setBlock(pos,Blocks.BLUE_ICE.defaultBlockState(),3);
        p.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES,pos.getCenter());
        h.assertTrue(((FluidFieldToolItem)fire.getItem()).pulse(p,fire)&&h.getLevel().getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER),"Fire drill melts blue ice into water");
        var water=new ItemStack(ModItems.EXCAVATION_TOOLS.get("water").get());CustomData.update(DataComponents.CUSTOM_DATA,water,t->t.putInt("FieldWater",2));
        h.runAfterDelay(8,()->{
            p.setItemInHand(InteractionHand.MAIN_HAND,water);
            var lava=h.absolutePos(new BlockPos(8,2,4));var origin=h.absolutePos(new BlockPos(8,1,1));p.moveTo(origin.getX()+.5,origin.getY(),origin.getZ()+.5,0,0);
            h.getLevel().setBlock(lava,Blocks.LAVA.defaultBlockState(),3);
            p.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES,lava.getCenter());
            h.assertTrue(((FluidFieldToolItem)water.getItem()).pulse(p,water)&&h.getLevel().getBlockState(lava).is(Blocks.OBSIDIAN),"Water drill cools a lava source");
            h.assertTrue(FluidFieldToolItem.water(water)==1&&water.getDamageValue()==1,"Cooling consumes stored water and durability");
            h.runAfterDelay(8,()->{
                p.setItemInHand(InteractionHand.MAIN_HAND,fire);var ore=ModBlocks.ELEMENTAL_MINERAL_ORES.get("frozen_flashium_ore").get();h.getLevel().setBlock(lava,ore.defaultBlockState(),3);
                h.assertFalse(((FluidFieldToolItem)fire.getItem()).pulse(p,fire),"Melting never bypasses excavation of a frozen ore");
                h.assertTrue(h.getLevel().getBlockState(lava).is(ore),"Frozen deposit remains intact");h.succeed();
            });
        });
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void fieldBlastProtectsClaimsAndNeverDuplicatesRawLoot(GameTestHelper h){
        var p=player(h,"BlastDrops");var centre=h.absolutePos(new BlockPos(5,2,5));var positions=new ArrayList<BlockPos>();
        for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){
            var pos=centre.offset(x,0,z);positions.add(pos);h.getLevel().setBlock(pos,ModBlocks.ELEMENTAL_MINERAL_ORES.get("coronium_ore").get().defaultBlockState(),3);
        }
        var protectedPos=positions.getFirst();var guard=new Protection(p,protectedPos,false);NeoForge.EVENT_BUS.register(guard);
        try{
            var source=new net.minecraft.world.entity.item.PrimedTnt(h.getLevel(),centre.getX()+.5,centre.getY()+.5,centre.getZ()+.5,p);
            source.getPersistentData().putBoolean("SpectrobesFieldBlast",true);source.getPersistentData().putUUID("FieldBlastOwner",p.getUUID());
            h.getLevel().explode(source,source.getX(),source.getY(),source.getZ(),4,net.minecraft.world.level.Level.ExplosionInteraction.TNT);
            h.assertTrue(!h.getLevel().getBlockState(protectedPos).isAir(),"Claimed ore survives field blast");
            long destroyed=positions.stream().filter(pos->h.getLevel().getBlockState(pos).isAir()).count();h.assertTrue(destroyed>0,"The charge resolves unprotected deposits");
            var drops=h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(centre).inflate(3),e->!e.isRemoved()&&e.getItem().is(ModMineralLoot.ELEMENTALS.get("coronium_c").get()));
            h.assertTrue(drops.size()<=destroyed,"At most one clean item per destroyed ore");
            for(var drop:drops){var data=RawFossilItem.data(drop.getItem());h.assertTrue(data.getBoolean("ExcavationAutomatic")&&!data.getBoolean("ExcavationCrown")&&!data.getBoolean("ExcavationCooperative"),"Explosive loot has no medals");}
            h.assertTrue(h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(centre).inflate(3),e->com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationSpecimen.accepts(e.getItem())).isEmpty(),"Blast cannot drop raw and clean specimens together");
        }finally{NeoForge.EVENT_BUS.unregister(guard);}
        h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void pitonHarvestsPlaneAndRespectsProtectedBlock(GameTestHelper h){
        var p=player(h,"PitonTest");var tool=new ItemStack(ModItems.EXCAVATION_TOOLS.get("piton").get());p.setItemInHand(InteractionHand.MAIN_HAND,tool);
        for(int x=3;x<=5;x++)for(int y=1;y<=3;y++)h.setBlock(new BlockPos(x,y,4),Blocks.STONE);
        var protectedPos=h.absolutePos(new BlockPos(3,2,4));var guard=new Protection(p,protectedPos,false);NeoForge.EVENT_BUS.register(guard);
        try{
            h.assertTrue(p.gameMode.destroyBlock(h.absolutePos(new BlockPos(4,2,4))),"Centre mined normally");
            int left=0;for(int x=3;x<=5;x++)for(int y=1;y<=3;y++)if(!h.getLevel().getBlockState(h.absolutePos(new BlockPos(x,y,4))).isAir())left++;
            h.assertTrue(left==1&&h.getLevel().getBlockState(protectedPos).is(Blocks.STONE),"Protected block remains; other eight harvested");
            h.assertTrue(tool.getDamageValue()==8,"One durability per harvested block");
        }finally{NeoForge.EVENT_BUS.unregister(guard);}
        h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void waterTankIsTransactionalAndPortable(GameTestHelper h){
        var p=player(h,"VacuumTest");var tool=new ItemStack(ModItems.EXCAVATION_TOOLS.get("vacuum").get());p.setItemInHand(InteractionHand.MAIN_HAND,tool);
        var target=h.absolutePos(new BlockPos(4,2,4));h.getLevel().setBlock(target,Blocks.WATER.defaultBlockState(),3);
        p.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES,target.getCenter());
        var guard=new Protection(p,target,false);NeoForge.EVENT_BUS.register(guard);
        try{
            h.assertFalse(((FluidFieldToolItem)tool.getItem()).pulse(p,tool),"Protected source cannot be drained");
            h.assertTrue(FluidFieldToolItem.water(tool)==0&&tool.getDamageValue()==0,"Blocked drain consumes nothing");
        }finally{NeoForge.EVENT_BUS.unregister(guard);}
        h.runAfterDelay(8,()->{
            h.assertTrue(((FluidFieldToolItem)tool.getItem()).pulse(p,tool),"Water source absorbed");
            h.assertTrue(FluidFieldToolItem.water(tool)==1&&tool.getDamageValue()==1,"One bucket stored, one durability consumed");
            h.assertTrue(FluidFieldToolItem.water(tool.copy())==1,"Tank survives stack copies");h.succeed();
        });
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void cancelledPlacementRollsBackWithoutCost(GameTestHelper h){
        var p=player(h,"PlaceGuard");var pos=h.absolutePos(new BlockPos(4,2,4));h.getLevel().setBlock(pos,Blocks.BLUE_ICE.defaultBlockState(),3);
        var guard=new Protection(p,pos,true);NeoForge.EVENT_BUS.register(guard);
        try{
            h.assertFalse(FieldTools.replace(p,pos,Blocks.WATER.defaultBlockState(),new ItemStack(ModItems.EXCAVATION_TOOLS.get("fire").get())),"Placement hook can deny melting");
            h.assertTrue(h.getLevel().getBlockState(pos).is(Blocks.BLUE_ICE),"Denied operation restores the original block");
        }finally{NeoForge.EVENT_BUS.unregister(guard);}
        h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void gigaRequiresManualCatalogueAndNeverAwardsMedals(GameTestHelper h){
        var p=player(h,"GigaTest");var tool=new ItemStack(ModItems.EXCAVATION_TOOLS.get("giga").get());p.setItemInHand(InteractionHand.MAIN_HAND,tool);
        var pos=h.absolutePos(new BlockPos(4,2,4));var ore=ModBlocks.ELEMENTAL_MINERAL_ORES.get("coronium_ore").get();h.getLevel().setBlock(pos,ore.defaultBlockState(),3);
        var ctx=new UseOnContext(p,InteractionHand.MAIN_HAND,new BlockHitResult(pos.getCenter(),Direction.UP,pos,false));
        var giga=(ExtractionFieldToolItem)tool.getItem();
        h.assertTrue(giga.activate(ctx)==net.minecraft.world.InteractionResult.FAIL,"Unknown specimen rejected");
        h.assertTrue(h.getLevel().getBlockState(pos).is(ore)&&tool.getDamageValue()==0,"Rejected extraction preserves ore and tool");
        var skill=p.getData(ModAttachments.EXCAVATION.get());skill.complete("coronium_c",50);
        h.assertTrue(giga.activate(ctx)==net.minecraft.world.InteractionResult.CONSUME,"Known specimen extracted");
        h.assertTrue(h.getLevel().getBlockState(pos).isAir()&&tool.getDamageValue()==8,"Ore consumed once and tool charged once");
        h.assertTrue(skill.completed()==1&&skill.xp()==50,"Automatic extraction gives no manual credit");
        var raw=ModItems.RAW_CORONA_FOSSIL.get().getDefaultInstance();CustomData.update(DataComponents.CUSTOM_DATA,raw,t->t.putString("Fossil","komainu"));
        var clean=FieldTools.clean(raw,85);var data=RawFossilItem.data(clean);
        h.assertTrue(!clean.isEmpty()&&data.getInt("ExcavationIntegrity")==85&&!data.getBoolean("ExcavationCrown")&&!data.getBoolean("ExcavationCooperative"),"Automatic result has integrity, no medals");
        h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void reusableChargeHasFuseAndConsumesEightDurability(GameTestHelper h){
        var p=player(h,"BlastTool");var tool=new ItemStack(ModItems.EXCAVATION_TOOLS.get("blast").get());p.setItemInHand(InteractionHand.MAIN_HAND,tool);
        var pos=h.absolutePos(new BlockPos(4,1,4));h.getLevel().setBlock(pos,Blocks.STONE.defaultBlockState(),3);
        var ctx=new UseOnContext(p,InteractionHand.MAIN_HAND,new BlockHitResult(pos.getCenter(),Direction.UP,pos,false));
        h.assertTrue(((ExtractionFieldToolItem)tool.getItem()).activate(ctx)==net.minecraft.world.InteractionResult.CONSUME,"Charge placed");
        h.assertTrue(tool.getCount()==1&&tool.getDamageValue()==8,"Reusable tool remains after placing a charge");
        var charges=h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.PrimedTnt.class,new AABB(pos).inflate(2));
        h.assertTrue(charges.size()==1&&charges.getFirst().getFuse()==60&&charges.getFirst().getPersistentData().getUUID("FieldBlastOwner").equals(p.getUUID()),"Charge keeps fuse and owner");
        charges.forEach(net.minecraft.world.entity.Entity::discard);h.succeed();
    }
}
