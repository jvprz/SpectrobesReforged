package com.jvprz.spectrobesreforged.common.gametest;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.vortex.*;
import com.jvprz.spectrobesreforged.common.content.entity.krawl.*;
import com.jvprz.spectrobesreforged.common.content.block.entity.ExcavationTableBlockEntity;
import com.jvprz.spectrobesreforged.common.feature.excavation.*;
import com.jvprz.spectrobesreforged.common.feature.krawl.*;
import com.jvprz.spectrobesreforged.common.feature.krawl.factory.KrawlFactory;
import com.jvprz.spectrobesreforged.common.registry.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.*;
import java.util.*;

@GameTestHolder(SpectrobesReforged.MODID)
@PrefixGameTestTemplate(false)
public final class CooperativeGameplayTests {
    private record Raid(KrawlVortexEntity controller,VortexProgress progress,ServerPlayer owner,List<KrawlEntity> mobs) {}
    private static ServerPlayer player(GameTestHelper h,String name){
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),name));p.setGameMode(GameType.SURVIVAL);p.setHealth(20);
        var pos=h.absolutePos(new BlockPos(12,1,12));p.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,0,0);return p;
    }
    private static Raid raid(GameTestHelper h){
        var owner=player(h,"RaidOwner");var level=h.getLevel();var vortex=ModEntities.KRAWL_VORTEX.get().create(level);
        vortex.moveTo(owner.getX(),owner.getY(),owner.getZ(),0,0);level.addFreshEntity(vortex);
        var definition=KrawlRegistry.getByKey(VortexClimate.krawl(level.getBiome(owner.blockPosition())));var mobs=new ArrayList<KrawlEntity>();
        for(int i=0;i<3;i++){
            var mob=KrawlFactory.create(level,definition);var pos=h.absolutePos(new BlockPos(6+i*5,1,6));mob.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,0,0);
            mob.getPersistentData().putUUID(KrawlVortexEntity.ENCOUNTER_TAG,vortex.getUUID());level.addFreshEntity(mob);mobs.add(mob);
        }
        var progress=new VortexProgress(owner.getUUID(),mobs.stream().map(KrawlEntity::getUUID).toList());VortexEncounters.get(level).begin(vortex.getUUID(),progress);
        return new Raid(vortex,progress,owner,mobs);
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void soloDeathCancelsAndRemovesAllKrawlImmediately(GameTestHelper h){
        var raid=raid(h);VortexEvents.onDeath(new net.neoforged.neoforge.event.entity.living.LivingDeathEvent(raid.owner(),h.getLevel().damageSources().genericKill()));
        h.assertTrue(raid.progress().status()==VortexProgress.Status.CANCELLED,"Solo death must cancel immediately");
        h.assertTrue(raid.controller().isRemoved()&&raid.mobs().stream().allMatch(KrawlEntity::isRemoved),"Raid and enemies must disappear");
        h.assertFalse(raid.progress().claim(raid.owner().getUUID()),"Wipe cannot authorize a reward");h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void latePlayerAddsThreeOnceAndDeathDoesNotWipeTheirAlly(GameTestHelper h){
        var raid=raid(h);var ally=player(h,"RaidAlly");
        h.assertTrue(raid.controller().tryJoinRaid(ally),"Nearby ally must join");h.assertTrue(raid.progress().members().size()==6,"Exactly three more enemies");
        h.assertFalse(raid.controller().tryJoinRaid(ally),"Re-entry cannot add enemies");
        VortexEvents.onDeath(new net.neoforged.neoforge.event.entity.living.LivingDeathEvent(raid.owner(),h.getLevel().damageSources().genericKill()));
        h.assertTrue(raid.progress().status()==VortexProgress.Status.ACTIVE&&!raid.controller().isRemoved(),"Surviving ally must keep the raid");
        var deadReward=VortexRewards.budget(2,raid.progress().participant(raid.owner().getUUID()).deaths());
        h.assertTrue(deadReward.c()<VortexRewards.budget(2,0).c(),"Death penalty must persist");
        VortexEvents.onDeath(new net.neoforged.neoforge.event.entity.living.LivingDeathEvent(ally,h.getLevel().damageSources().genericKill()));
        h.assertTrue(raid.controller().isRemoved(),"Everyone falling must end the raid");
        for(var id:raid.progress().members())h.assertTrue(h.getLevel().getEntity(id)==null||h.getLevel().getEntity(id).isRemoved(),"No surviving encounter enemy");h.succeed();
    }
    @GameTest(template="server_smoke",timeoutTicks=100)
    public static void excavationAllowsTwoOperatorsButOneHolderPerTool(GameTestHelper h){
        h.setBlock(2,0,2,net.minecraft.world.level.block.Blocks.STONE);h.setBlock(2,1,2,ModBlocks.EXCAVATION_TABLE.get());
        var table=(ExcavationTableBlockEntity)h.getLevel().getBlockEntity(h.absolutePos(new BlockPos(2,1,2)));
        var a=player(h,"DiggerA");var b=player(h,"DiggerB");var pos=h.absolutePos(new BlockPos(2,1,3));a.moveTo(pos.getX(),pos.getY(),pos.getZ());b.moveTo(pos.getX(),pos.getY(),pos.getZ());
        h.assertTrue(table.claim(a)&&table.claim(b),"Both operators must open the same table");
        a.containerMenu=new ExcavationMenu(1,a.getInventory(),table,table.dataFor(a));b.containerMenu=new ExcavationMenu(2,b.getInventory(),table,table.dataFor(b));
        table.setItem(0,new ItemStack(ModItems.RAW_CORONA_FOSSIL.get()));
        int opening=table.data().get(9);h.assertTrue(opening>0,"Specimen should start buried");
        table.work(a,15,15,ExcavationPuzzle.TAP);int afterA=table.data().get(9);
        table.work(b,15,15,ExcavationPuzzle.TAP);h.assertTrue(table.data().get(9)==afterA,"Second operator cannot use a reserved tool");
        table.cursor(a,75,75,ExcavationPuzzle.TAP,false);table.work(b,15,15,ExcavationPuzzle.TAP);
        h.assertTrue(table.data().get(9)<afterA,"Tool release allows the other operator to continue the same specimen");
        table.release(a);table.release(b);a.containerMenu=a.inventoryMenu;b.containerMenu=b.inventoryMenu;h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void raidRewardsWaitForAllEnemiesAndCannotBePaidTwice(GameTestHelper h){
        var raid=raid(h);var ally=player(h,"RewardAlly");
        h.assertTrue(raid.controller().tryJoinRaid(ally),"Ally should add a full wave");
        VortexEvents.onDeath(new net.neoforged.neoforge.event.entity.living.LivingDeathEvent(raid.owner(),h.getLevel().damageSources().genericKill()));
        VortexEncounters.payPending(raid.owner());h.assertTrue(raid.owner().getInventory().isEmpty(),"No reward before completion");
        for(var id:raid.progress().members()){
            var mob=(KrawlEntity)h.getLevel().getEntity(id);mob.hurt(h.getLevel().damageSources().genericKill(),10000);
        }
        h.assertTrue(raid.progress().remaining()==0,"Confirmed deaths should clear the raid");
        raid.progress().advance(1,Set.of(ally.getUUID()));
        VortexEncounters.payPending(raid.owner());VortexEncounters.payPending(ally);
        int first=raid.owner().getInventory().items.stream().mapToInt(ItemStack::getCount).sum();
        int second=ally.getInventory().items.stream().mapToInt(ItemStack::getCount).sum();
        h.assertTrue(first==2&&second==3,"Each participant needs their own death-adjusted reward");
        VortexEncounters.payPending(raid.owner());VortexEncounters.payPending(ally);
        h.assertTrue(first==raid.owner().getInventory().items.stream().mapToInt(ItemStack::getCount).sum(),"Reward must be once per UUID");
        h.assertTrue(second==ally.getInventory().items.stream().mapToInt(ItemStack::getCount).sum(),"Ally reward must also be once");
        raid.controller().finishEncounter(raid.progress());h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void tableDisplaySynchronizesSingleItemWithoutPrivateBoardAndSurvivesRotation(GameTestHelper h) {
        h.setBlock(2,1,2,ModBlocks.EXCAVATION_TABLE.get());
        var pos=h.absolutePos(new BlockPos(2,1,2));
        var table=(ExcavationTableBlockEntity)h.getLevel().getBlockEntity(pos);
        var raw=new ItemStack(ModItems.RAW_CORONA_FOSSIL.get(),3);
        table.setItem(0,raw);
        table.data().get(9); // Initialize and save the excavation board onto the actual raw item.
        var update=table.getUpdateTag(h.getLevel().registryAccess());
        var display=ItemStack.parseOptional(h.getLevel().registryAccess(),update.getCompound("DisplayItem"));
        h.assertTrue(display.is(ModItems.RAW_CORONA_FOSSIL.get())&&display.getCount()==1,"Chunk watchers need one visible specimen");
        h.assertTrue(!display.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA),"Display must not expose the board, seed or private specimen data");
        for(var facing:net.minecraft.core.Direction.Plane.HORIZONTAL) {
            var state=ModBlocks.EXCAVATION_TABLE.get().defaultBlockState().setValue(com.jvprz.spectrobesreforged.common.content.block.ExcavationTableBlock.FACING,facing);
            h.getLevel().setBlock(pos,state,3);
            h.assertTrue(h.getLevel().getBlockEntity(pos)==table&&table.getItem(0).getCount()==3,"Facing updates must retain the same inventory and block entity");
        }
        var clientCopy=new ExcavationTableBlockEntity(pos,table.getBlockState());
        clientCopy.handleUpdateTag(update,h.getLevel().registryAccess());
        table.removeItem(0,3);
        h.assertTrue(!table.getUpdateTag(h.getLevel().registryAccess()).contains("DisplayItem"),"Removing the specimen must clear its display");
        table.setItem(1,new ItemStack(ModItems.POWER_C.get()));
        display=ItemStack.parseOptional(h.getLevel().registryAccess(),table.getUpdateTag(h.getLevel().registryAccess()).getCompound("DisplayItem"));
        h.assertTrue(display.is(ModItems.POWER_C.get()),"Completed output should remain visible until collected");
        table.removeItem(1,1);
        h.assertTrue(table.displayStack().isEmpty(),"Collecting output must empty the work surface");
        h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void cleanOutputTakesDisplayPriorityAndBlocksQueuedExcavation(GameTestHelper h) {
        h.setBlock(2,1,2,ModBlocks.EXCAVATION_TABLE.get());
        var pos=h.absolutePos(new BlockPos(2,1,2));
        var table=(ExcavationTableBlockEntity)h.getLevel().getBlockEntity(pos);
        var a=player(h,"QueuedDigger");a.moveTo(pos.getX(),pos.getY(),pos.getZ()+1);
        h.assertTrue(table.claim(a),"Operator should open table");
        a.containerMenu=new ExcavationMenu(1,a.getInventory(),table,table.dataFor(a));
        table.setItem(1,new ItemStack(ModItems.POWER_C.get()));
        table.setItem(0,new ItemStack(ModItems.RAW_CORONA_FOSSIL.get()));
        h.assertTrue(table.displayStack().is(ModItems.POWER_C.get()),"Clean output must take visual priority over queued raw input");
        h.assertTrue(table.data().get(3)==3,"Client must see occupied output, not an active board");
        table.work(a,15,15,ExcavationPuzzle.TAP);
        ExcavationTableBlockEntity.serverTick(h.getLevel(),pos,table.getBlockState(),table);
        h.assertTrue(!table.getItem(0).has(net.minecraft.core.component.DataComponents.CUSTOM_DATA),"Blocked raw piece must not initialize, tick or accept tool strokes");
        table.removeItem(1,1);
        h.assertTrue(table.displayStack().is(ModItems.RAW_CORONA_FOSSIL.get()),"Queued raw piece becomes visible when output is collected");
        h.assertTrue(table.data().get(3)==1,"Excavation resumes after collecting clean output");
        // FakePlayer is not in ServerLevel.players(), so the preceding server tick
        // releases its session just like an offline player. Reopen before resuming.
        h.assertTrue(table.claim(a),"Operator can reopen after the output is collected");
        int opening=table.data().get(9);table.work(a,15,15,ExcavationPuzzle.TAP);
        h.assertTrue(table.data().get(9)<opening,"Queued piece must accept tools after unblocking");
        table.removeItem(0,1);
        h.assertTrue(table.displayStack().isEmpty()&&!table.getUpdateTag(h.getLevel().registryAccess()).contains("DisplayItem"),"Empty table must clear the displayed item for chunk watchers");
        h.assertTrue(table.data().get(3)==0,"Empty table must show the idle interface");
        table.release(a);a.containerMenu=a.inventoryMenu;h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void cooperationRequiresWorkAndPersistsOnTheSpecimen(GameTestHelper h) {
        h.setBlock(2,1,2,ModBlocks.EXCAVATION_TABLE.get());var pos=h.absolutePos(new BlockPos(2,1,2));
        var table=(ExcavationTableBlockEntity)h.getLevel().getBlockEntity(pos);
        var a=player(h,"BadgeDiggerA");var b=player(h,"BadgeDiggerB");
        a.moveTo(pos.getX(),pos.getY(),pos.getZ()+1);b.moveTo(pos.getX(),pos.getY(),pos.getZ()+1);
        table.claim(a);table.claim(b);table.setItem(0,new ItemStack(ModItems.RAW_CORONA_FOSSIL.get()));
        table.work(a,15,15,ExcavationPuzzle.TAP);
        var saved=com.jvprz.spectrobesreforged.common.content.item.RawFossilItem.data(table.getItem(0)).getCompound("Excavation");
        h.assertTrue(saved.getList("Contributors",10).size()==1,"Watching alone must not count as cooperation");
        table.cursor(a,75,75,ExcavationPuzzle.TAP,false);table.work(b,15,15,ExcavationPuzzle.TAP);
        var raw=table.removeItem(0,1);
        var data=com.jvprz.spectrobesreforged.common.content.item.RawFossilItem.data(raw);
        saved=data.getCompound("Excavation");
        h.assertTrue(saved.getList("Contributors",10).size()==2,"Two successful operators must persist on the raw item");
        // Finish the board in this fixture while retaining the real saved contributor metadata.
        var cells=saved.getByteArray("Cells");for(int i=0;i<cells.length;i++)cells[i]=(byte)(cells[i]&ExcavationPuzzle.FOSSIL);
        saved.putByteArray("Cells",cells);saved.putInt("Overburden",0);data.put("Excavation",saved);
        raw.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.of(data));
        table.setItem(0,raw);
        h.assertTrue(table.extract(a),"Restored specimen can be extracted");
        h.assertTrue(com.jvprz.spectrobesreforged.common.content.item.RawFossilItem.data(table.getItem(1)).getBoolean("ExcavationCooperative"),"Clean fossil must retain its cooperative badge");
        table.removeItem(1,1);table.setItem(0,new ItemStack(ModItems.RAW_CORONA_FOSSIL.get()));table.data().get(9);
        h.assertTrue(com.jvprz.spectrobesreforged.common.content.item.RawFossilItem.data(table.getItem(0)).getCompound("Excavation").getList("Contributors",10).isEmpty(),"Another specimen must not inherit contributors");
        table.release(a);table.release(b);h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void manualDiscoveryCreditsBothWorkersOnceAndSkipsAutomaticExtraction(GameTestHelper h) {
        // NeoForge intentionally refuses advancement awards to FakePlayer instances.
        // Use real server-player state with a no-network test connection instead.
        var a=new ServerPlayer(h.getLevel().getServer(),h.getLevel(),new GameProfile(UUID.randomUUID(),"MilestoneA"),net.minecraft.server.level.ClientInformation.createDefault());
        var b=new ServerPlayer(h.getLevel().getServer(),h.getLevel(),new GameProfile(UUID.randomUUID(),"MilestoneB"),net.minecraft.server.level.ClientInformation.createDefault());
        a.connection=player(h,"MilestoneConnA").connection;b.connection=player(h,"MilestoneConnB").connection;
        a.setGameMode(GameType.SURVIVAL);b.setGameMode(GameType.SURVIVAL);
        var advancement=h.getLevel().getServer().getAdvancements().get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"excavate"));
        var perfect=h.getLevel().getServer().getAdvancements().get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"excavation_perfect"));
        com.jvprz.spectrobesreforged.common.feature.progression.ModProgression.completedExcavation(List.of(a,b,a),"komainu_fossil",100,true,2);
        for(var p:List.of(a,b)) {
            var skill=p.getData(com.jvprz.spectrobesreforged.common.registry.ModAttachments.EXCAVATION.get());
            h.assertTrue(skill.completed()==1&&skill.xp()==100&&skill.knows("komainu_fossil"),"Each worker receives one discovery and normal skill XP");
            h.assertTrue(p.getAdvancements().getOrStartProgress(advancement).isDone(),"Both workers earn the existing excavation advancement");
            h.assertTrue(p.getAdvancements().getOrStartProgress(perfect).isDone(),"Both workers earn the perfect finding advancement");
        }
        com.jvprz.spectrobesreforged.common.feature.progression.ModProgression.completedExcavation(List.of(a,b),"komainu_fossil",0,true,2);
        h.assertTrue(a.getData(com.jvprz.spectrobesreforged.common.registry.ModAttachments.EXCAVATION.get()).completed()==1,"Automatic or repeated empty extraction must not add discovery credit");
        h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void childrenRejectEnvironmentalAndCombatDamage(GameTestHelper h) {
        var child=ModEntities.SPECTROBE.get().create(h.getLevel());child.setSpeciesKey("komainu");
        child.setStage(com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeStage.CHILD);
        var pos=h.absolutePos(new BlockPos(12,1,12));child.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,0,0);h.getLevel().addFreshEntity(child);
        float hp=child.getHealth();var damage=h.getLevel().damageSources();
        for(var source:List.of(damage.generic(),damage.lava(),damage.inFire(),damage.fall(),damage.drown(),damage.inWall(),damage.starve(),damage.wither(),damage.outOfBorder(),damage.fellOutOfWorld())) {
            h.assertTrue(!child.hurt(source,10000),"Child must reject every damage source");
            h.assertTrue(child.getHealth()==hp&&child.isAlive(),"Protected Child must retain its health");
        }
        h.assertTrue(!child.causeFallDamage(100,1,damage.fall()),"Falling must not trigger damage");
        child.setRemainingFireTicks(100);h.assertTrue(!child.isOnFire()&&child.fireImmune(),"Child should not render as burning");
        child.discard();h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void normalAndPiercingArrowsBounceOffIdleVortex(GameTestHelper h) {
        var level=h.getLevel();var pos=h.absolutePos(new BlockPos(12,1,12));
        var vortex=ModEntities.KRAWL_VORTEX.get().create(level);vortex.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,0,0);level.addFreshEntity(vortex);
        var arrows=new ArrayList<net.minecraft.world.entity.projectile.Arrow>();
        for(int i=0;i<2;i++) {
            var arrow=new net.minecraft.world.entity.projectile.Arrow(net.minecraft.world.entity.EntityType.ARROW,level);
            arrow.setPos(vortex.getX()+(i==0?-.3:.3),vortex.getY()+1.3,vortex.getZ()-5);arrow.setNoGravity(true);
            arrow.setDeltaMovement(0,0,2);if(i==1){var tag=arrow.saveWithoutId(new net.minecraft.nbt.CompoundTag());tag.putByte("PierceLevel",(byte)3);arrow.load(tag);}level.addFreshEntity(arrow);arrows.add(arrow);
        }
        h.runAfterDelay(5,()->{
            for(var arrow:arrows)h.assertTrue(arrow.isAlive()&&arrow.getDeltaMovement().z<0&&Math.abs(arrow.getDeltaMovement().z)<.5,"Arrow must bounce back with vanilla shield damping");
            h.assertTrue(vortex.isAlive()&&VortexEncounters.get(level).find(vortex.getUUID())==null,"Arrow hit must neither destroy the vortex nor start a raid");
            arrows.forEach(net.minecraft.world.entity.Entity::discard);vortex.discard();h.succeed();
        });
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void everyEnvironmentalBlockDropsItsOwnPortableSpecimen(GameTestHelper h) {
        var worker=player(h,"GeologyDrops");var pos=h.absolutePos(new BlockPos(2,1,2));
        for(var deposit:com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationDeposits.all()) {
            var block=ModBlocks.ENVIRONMENTAL_ORES.get(deposit.id()).get();
            var expected=ModItems.ENVIRONMENTAL_RAWS.get(deposit.rawId()).get();
            var drops=net.minecraft.world.level.block.Block.getDrops(block.defaultBlockState(),h.getLevel(),pos,null,worker,new ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE));
            h.assertTrue(drops.size()==1&&drops.getFirst().is(expected),"Environmental ore must drop exactly its matching raw item: "+deposit.id());
            var raw=drops.getFirst();var tag=com.jvprz.spectrobesreforged.common.content.item.RawFossilItem.data(raw);
            h.assertTrue(tag.getInt("ExcavationTerrain")==deposit.terrain()&&tag.getInt("ExcavationRock")==deposit.rock(),"Drop must preserve host geology");
            var candidates=ExcavationSpecimen.candidates(raw);
            h.assertTrue(candidates.contains(tag.getString(deposit.fossil()?"Fossil":"Mineral")),"Fixed species/mineral must match its raw item's element/grade");
            var creative=expected.getDefaultInstance();
            h.assertTrue(com.jvprz.spectrobesreforged.common.content.item.RawFossilItem.data(creative).getInt("ExcavationTerrain")==deposit.terrain(),"Creative raw must carry its own geology without needing a mined block");
        }
        h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void allRawVariantsTriggerTheExistingObtainingMilestones(GameTestHelper h) {
        var p=new ServerPlayer(h.getLevel().getServer(),h.getLevel(),new GameProfile(UUID.randomUUID(),"RawMilestones"),net.minecraft.server.level.ClientInformation.createDefault());
        p.connection=player(h,"RawMilestoneConn").connection;p.setGameMode(GameType.SURVIVAL);
        var manager=h.getLevel().getServer().getAdvancements();
        var first=manager.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"first_fossil"));
        var root=manager.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"root"));
        var items=new ArrayList<net.minecraft.world.item.Item>();
        items.addAll(List.of(ModItems.RAW_CORONA_FOSSIL.get(),ModItems.RAW_AURORA_FOSSIL.get(),ModItems.RAW_FLASH_FOSSIL.get(),ModItems.RAW_C_MINERAL.get(),ModItems.RAW_B_MINERAL.get(),ModItems.RAW_A_MINERAL.get(),ModItems.RAW_A_PLUS_MINERAL.get()));
        ModItems.ENVIRONMENTAL_RAWS.values().forEach(item->items.add(item.get()));
        for(var item:items) {
            p.getAdvancements().revoke(first,"completed");p.getAdvancements().revoke(root,"completed");p.getInventory().clearContent();
            var stack=item.getDefaultInstance();p.getInventory().setItem(0,stack);
            net.minecraft.advancements.CriteriaTriggers.INVENTORY_CHANGED.trigger(p,p.getInventory(),stack);
            h.assertTrue(p.getAdvancements().getOrStartProgress(root).isDone(),"Every raw item should unlock the existing advancement tree");
            h.assertTrue(p.getAdvancements().getOrStartProgress(first).isDone()==(item instanceof com.jvprz.spectrobesreforged.common.content.item.RawFossilItem),"Only fossil raws should award the first fossil milestone");
        }
        h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void handheldBlowerPushesForwardWithoutDamageAndConsumesDurability(GameTestHelper h) {
        var p=player(h,"BlowerUser");p.setYRot(0);p.setXRot(0);
        var item=ModItems.EXCAVATION_TOOLS.get("blower").get();var stack=item.getDefaultInstance();p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);
        var front=new net.minecraft.world.entity.monster.Zombie(net.minecraft.world.entity.EntityType.ZOMBIE,h.getLevel());front.setNoAi(true);
        front.moveTo(p.getX(),p.getY(),p.getZ()+3,0,0);h.getLevel().addFreshEntity(front);float hp=front.getHealth();
        var back=new net.minecraft.world.entity.monster.Zombie(net.minecraft.world.entity.EntityType.ZOMBIE,h.getLevel());back.setNoAi(true);
        back.moveTo(p.getX(),p.getY(),p.getZ()-3,0,0);h.getLevel().addFreshEntity(back);
        item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(front.getDeltaMovement().z>0&&back.getDeltaMovement().lengthSqr()==0,"Air must push only mobs in front of the user");
        h.assertTrue(front.getHealth()==hp,"Blower must not directly damage mobs");
        h.assertTrue(stack.isDamageableItem()&&stack.getMaxDamage()==256&&stack.getDamageValue()==1,"One burst must consume one durability");
        item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(stack.getDamageValue()==1,"Repeated interaction packets must not duplicate a burst in the same tick");
        p.stopUsingItem();front.discard();back.discard();h.succeed();
    }
    @GameTest(template="raid_arena",timeoutTicks=100)
    public static void handheldBlowerStopsAtWallsAndBreaksOnItsLastUse(GameTestHelper h) {
        var p=player(h,"BlowerWall");p.setYRot(0);p.setXRot(0);
        var item=ModItems.EXCAVATION_TOOLS.get("blower").get();var stack=item.getDefaultInstance();stack.setDamageValue(255);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);
        var target=new net.minecraft.world.entity.monster.Zombie(net.minecraft.world.entity.EntityType.ZOMBIE,h.getLevel());target.setNoAi(true);target.moveTo(p.getX(),p.getY(),p.getZ()+4,0,0);h.getLevel().addFreshEntity(target);
        var wall=net.minecraft.core.BlockPos.containing(p.getX(),p.getY(),p.getZ()+2);
        for(int dy=0;dy<3;dy++)h.getLevel().setBlock(wall.above(dy),net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),3);
        item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(target.getDeltaMovement().lengthSqr()==0,"Air must not push through solid blocks");
        h.assertTrue(p.getMainHandItem().isEmpty(),"Tool must break after its final durability point");
        target.discard();h.succeed();
    }
}
