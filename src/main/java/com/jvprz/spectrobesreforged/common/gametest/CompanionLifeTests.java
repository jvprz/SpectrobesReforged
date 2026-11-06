package com.jvprz.spectrobesreforged.common.gametest;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.CompanionLifeGoals;
import com.jvprz.spectrobesreforged.common.registry.ModEntities;
import com.jvprz.spectrobesreforged.common.registry.ModItems;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.*;
import java.util.Optional;
import java.util.UUID;

@GameTestHolder(SpectrobesReforged.MODID)
@PrefixGameTestTemplate(false)
public final class CompanionLifeTests {
    private static ServerPlayer owner(GameTestHelper h) {
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"LifeOwner"));
        p.setGameMode(GameType.SURVIVAL);p.setHealth(20);var pos=h.absolutePos(new BlockPos(12,2,12));
        p.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,0,0);return p;
    }
    private static SpectrobeEntity companion(GameTestHelper h,ServerPlayer owner,int x) {
        var mob=new SpectrobeEntity(ModEntities.SPECTROBE.get(),h.getLevel()) {
            @Override public Optional<Player> getOwner() {return Optional.of(owner);}
        };
        mob.setSpeciesKey("komainu");mob.setOwner(owner);
        var pos=h.absolutePos(new BlockPos(x,2,12));mob.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,0,0);
        h.getLevel().addFreshEntity(mob);return mob;
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void playfulHopYieldsToMineralsScanningAndOtherSpecies(GameTestHelper h) {
        var owner=owner(h);var mob=companion(h,owner,10);mob.setOnGround(true);
        var goal=new CompanionLifeGoals.KomainuPlayfulJump(mob);
        goal.start();
        h.assertTrue(mob.getLifeGesture()==3&&goal.canContinueToUse(),"Komainu can finish one idle hop");
        owner.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(ModItems.POWER_C.get()));
        h.assertFalse(goal.canContinueToUse(),"An offered mineral must interrupt the hop");
        owner.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,ItemStack.EMPTY);
        mob.triggerRangePause(30);
        h.assertFalse(goal.canContinueToUse(),"Scanning must interrupt the hop");goal.stop();
        h.assertTrue(mob.getLifeGesture()==0,"No stale jumping gesture after interruption");
        mob.setSpeciesKey("spiko");
        h.assertFalse(goal.canUse(),"Other species must never use Komainu's hop");
        mob.discard();h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void mineralAttentionNeverConsumesItemsAndYieldsToScanning(GameTestHelper h) {
        var owner=owner(h);var mob=companion(h,owner,10);owner.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(ModItems.POWER_C.get(),4));
        var goal=new CompanionLifeGoals.WatchMineral(mob);
        h.assertTrue(goal.canUse(),"Offered mineral must attract attention");goal.start();goal.tick();
        h.assertTrue(mob.getLifeGesture()==2 && owner.getMainHandItem().getCount()==4,"Attention is cosmetic, no feeding");
        mob.triggerRangePause(30);
        h.assertFalse(goal.canContinueToUse(),"Scanner must preempt mineral attention");
        h.assertTrue(mob.getLifeGesture()==0,"Scanner clears ambient gesture");goal.stop();mob.discard();h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void curiosityEndsWhenOwnerLeavesAndDoesNotStartAScan(GameTestHelper h) {
        var owner=owner(h);var mob=companion(h,owner,10);var goal=new CompanionLifeGoals.CuriousPause(mob);
        goal.start();goal.tick();
        h.assertTrue(mob.getLifeGesture()==1 && !mob.isRangePaused(),"Curiosity cannot start gameplay scanning");
        owner.moveTo(owner.getX()+12,owner.getY(),owner.getZ(),0,0);
        h.assertFalse(goal.canContinueToUse(),"Leaving owner ends curiosity");goal.stop();
        h.assertTrue(mob.getLifeGesture()==0,"Stopping releases the cosmetic state");mob.discard();h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=140)
    public static void companionActuallyFollowsAndThenHoldsItsScannerPosition(GameTestHelper h) {
        var owner=owner(h);var mob=companion(h,owner,4);double initial=mob.distanceToSqr(owner);
        h.runAfterDelay(60,()->{
            h.assertTrue(mob.distanceToSqr(owner)<initial-4,"Real server navigation must approach owner");
            mob.triggerRangePause(30);var center=mob.position();
            h.runAfterDelay(10,()->{
                h.assertTrue(mob.position().distanceToSqr(center)<.02,"Ambient goals must not move a scanning companion");
                h.assertTrue(mob.getLifeGesture()==0,"Scanning suppresses ambient poses");mob.discard();h.succeed();
            });
        });
    }

    private static SpectrobeEntity linked(GameTestHelper h,ServerPlayer owner,int x) {
        var mob=companion(h,owner,x);var id=UUID.randomUUID();
        owner.getData(com.jvprz.spectrobesreforged.common.registry.ModAttachments.PRIZMOD.get()).setBabySlot(
            new com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry(id,"komainu",0,"CHILD",1,60,60,40,30,0,0,0,0));
        mob.getPersistentData().putUUID("SpectrobeId",id);
        h.assertTrue(com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.linkedOwner(mob)!=null,"Linked owner must resolve");
        h.assertTrue(com.jvprz.spectrobesreforged.common.config.Config.fetchMinerals(),"Fetch config must be enabled");
        return mob;
    }
    private static net.minecraft.world.entity.item.ItemEntity dropped(GameTestHelper h,SpectrobeEntity mob,int count) {
        var item=new net.minecraft.world.entity.item.ItemEntity(h.getLevel(),mob.getX()+.5,mob.getY()+.2,mob.getZ(),new ItemStack(ModItems.POWER_C.get(),count));
        item.setPickUpDelay(0);h.getLevel().addFreshEntity(item);return item;
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void fetchingTakesOneRespectsReservationAndRetrievesExactlyOnce(GameTestHelper h) {
        var owner=owner(h);var mob=linked(h,owner,10);var item=dropped(h,mob,2);
        item.setTarget(UUID.randomUUID());h.assertFalse(com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.pickUp(mob,item),"Reserved item belongs to someone else");
        item.setTarget(owner.getUUID());
        h.assertTrue(com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.eligible(item,owner),"Eligible fixture");
        var ray=h.getLevel().clip(new net.minecraft.world.level.ClipContext(mob.getEyePosition(),item.position().add(0,.1,0),net.minecraft.world.level.ClipContext.Block.COLLIDER,net.minecraft.world.level.ClipContext.Fluid.NONE,mob));
        h.assertTrue(ray.getType()==net.minecraft.world.phys.HitResult.Type.MISS,"Pickup sight: "+ray.getType()+" "+ray.getBlockPos()+" mob="+mob.position()+" item="+item.position());
        h.assertTrue(com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.pickUp(mob,item),"Owner-reserved mineral can be fetched");
        h.assertTrue(item.getItem().getCount()==1 && mob.getMainHandItem().getCount()==1,"Take one unit only");
        h.assertFalse(mob.claimCarriedMineral(owner(h)),"Other players cannot claim it");
        h.assertTrue(mob.claimCarriedMineral(owner),"Owner can collect it");
        h.assertFalse(mob.claimCarriedMineral(owner),"A second click cannot duplicate it");
        h.assertTrue(owner.getInventory().countItem(ModItems.POWER_C.get())==1,"Exactly one delivered mineral");
        h.assertTrue(com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.pickUp(mob,item),"Pick up remaining unit");
        owner.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(ModItems.POWER_C.get(),4));
        owner.interactOn(mob,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertFalse(mob.hasCarriedMineral(),"Right click must retrieve even when holding a mineral");
        h.assertTrue(owner.getData(com.jvprz.spectrobesreforged.common.registry.ModAttachments.PRIZMOD.get()).getBabySlot().orElseThrow().mineralsFed()==0,"Retrieval cannot accidentally feed the held mineral");
        mob.discard();h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void fullInventoryKeepsMineralAndScannerBlocksPickup(GameTestHelper h) {
        var owner=owner(h);var mob=linked(h,owner,10);var item=dropped(h,mob,1);
        mob.triggerRangePause(20);h.assertFalse(com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.pickUp(mob,item),"Scanner owns movement and pickup");
        mob.stopRangePause();h.assertTrue(com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.pickUp(mob,item),"Pickup resumes after scan");
        for(int i=0;i<36;i++)owner.getInventory().setItem(i,new ItemStack(net.minecraft.world.item.Items.STONE,64));
        h.assertFalse(mob.claimCarriedMineral(owner),"Full inventory must not destroy the carried mineral");
        h.assertTrue(mob.hasCarriedMineral(),"Item remains in mouth");mob.discard();h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void carriedMineralSurvivesNbtAndIgnoredFoodUsesNormalTrainingOnce(GameTestHelper h) {
        var owner=owner(h);var mob=linked(h,owner,10);var item=dropped(h,mob,1);
        h.assertTrue(com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.pickUp(mob,item),"Must pick up fixture mineral");
        var saved=mob.saveWithoutId(new net.minecraft.nbt.CompoundTag());
        mob.remove(net.minecraft.world.entity.Entity.RemovalReason.UNLOADED_TO_CHUNK);
        var restored=new SpectrobeEntity(ModEntities.SPECTROBE.get(),h.getLevel()){
            @Override public Optional<Player> getOwner(){return Optional.of(owner);}
        };
        saved.putInt("CarriedMineralWait",com.jvprz.spectrobesreforged.common.config.Config.mineralPatienceTicks()-1);
        restored.load(saved);h.assertTrue(restored.hasCarriedMineral(),"Vanilla equipment serialization preserves the mineral");
        restored.tick();
        var data=owner.getData(com.jvprz.spectrobesreforged.common.registry.ModAttachments.PRIZMOD.get());
        h.assertFalse(restored.hasCarriedMineral(),"Ignored mineral must be eaten");
        h.assertTrue(restored.getReaction()==2,"Eating must trigger the eating animation state");
        h.assertTrue(data.getBabySlot().orElseThrow().mineralsFed()==1,"Normal feeding awarded once");
        restored.tick();h.assertTrue(data.getBabySlot().orElseThrow().mineralsFed()==1,"No repeated training from the same mineral");restored.discard();h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void dismissingCompanionRefundsItsMineralToTheOwner(GameTestHelper h) {
        var owner=owner(h);var mob=linked(h,owner,10);var item=dropped(h,mob,1);
        h.assertTrue(com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.pickUp(mob,item),"Pickup fixture");
        var center=mob.getBoundingBox().inflate(2);mob.discard();
        var refunds=h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,center,e->e.getItem().is(ModItems.POWER_C.get())&&owner.getUUID().equals(e.getTarget()));
        h.assertTrue(refunds.stream().mapToInt(e->e.getItem().getCount()).sum()==1,"Dismissal returns exactly one owner-reserved item");h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=140)
    public static void companionNavigatesToDroppedMineralAndBringsItBack(GameTestHelper h) {
        var owner=owner(h);var mob=linked(h,owner,10);var item=dropped(h,mob,1);item.setPos(mob.getX()-2,mob.getY()+.1,mob.getZ());
        // Keep this navigation fixture independent of item toss RNG and nearby parallel tests.
        item.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);item.setTarget(owner.getUUID());mob.getRandom().setSeed(17);
        h.succeedWhen(()->{
            h.assertTrue(mob.hasCarriedMineral(),"Companion must navigate to and fetch dropped mineral");
            h.assertTrue(mob.distanceToSqr(owner)<12,"Companion must bring mineral back toward owner");mob.discard();h.succeed();
        });
    }
}
