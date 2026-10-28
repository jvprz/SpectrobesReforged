package com.jvprz.spectrobesreforged.common.feature.training;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.*;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.*;
import com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationProgress;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class TrainingTest {
 private SpectrobeEntry entry(){return new SpectrobeEntry(UUID.randomUUID(),"komainu",0,"CHILD",1,120,100,65,55,0,0,0,0);}
 private SpectrobeSpecies species(){return new SpectrobeSpecies(1,"komainu",SpectrobeType.CORONA,SpectrobeStage.CHILD,3,new SpectrobeStats(new StatGrowth(120,200,1),new StatGrowth(65,125,1),new StatGrowth(55,105,1),0,0,null),null,List.of());}
 @Test void qualityThresholdsAndBirthBonus(){
  int[] q={-1,0,69,70,84,85,94,95,100},v={0,0,0,1,1,2,2,3,3};
  for(int i=0;i<q.length;i++)assertEquals(v[i],SpectrobeLevels.qualityPercent(q[i]));
  var e=SpectrobeLevels.fromFossil(entry(),100,true);assertEquals(123,e.hp());assertEquals(66,e.atk());assertEquals(56,e.def());assertTrue(e.crowned());assertEquals(100,e.excavationQuality());
 }
 @Test void multiLevelGrowthPreservesInjuryAndStage(){
  var e=entry();var next=SpectrobeLevels.award(e,225,species());assertEquals(3,next.level());assertEquals(0,next.xp());assertEquals(20,next.hp()-next.hpCur());assertTrue(next.isChild());assertTrue(next.hp()>e.hp());
 }
 @Test void fractionalMineralCarriesIntoNextLevel(){
  var e=entry().withProgress(new SpectrobeProgress(90,false,-1,0));int xp=SpectrobeLevels.mineralXp(e,20);assertEquals(23,xp);var next=SpectrobeLevels.award(e,xp,species());assertEquals(2,next.level());assertEquals(13,next.xp());
 }
 @Test void capAndDeadEntries(){
  var dead=entry().withHpCur(0);var capped=SpectrobeLevels.award(dead,Integer.MAX_VALUE,species());assertEquals(100,capped.level());assertEquals(0,capped.xp());assertEquals(0,capped.hpCur());assertEquals(0,SpectrobeLevels.mineralXp(capped,60));assertEquals(capped,SpectrobeLevels.award(capped,100,species()));
 }
 @Test void legacyStatsAreNotReducedByGrowth(){
  var e=entry().withStats(999,900,999,999);var next=SpectrobeLevels.award(e,100,species());assertEquals(999,next.hp());assertEquals(999,next.atk());assertEquals(999,next.def());assertEquals(900,next.hpCur());
 }
 @Test void metadataSurvivesCopiesAndSave(){
  var e=entry().withProgress(new SpectrobeProgress(45,true,98,7)).withColor(2).withMineralsFed(6).withHpCur(80);
  var json=SpectrobeEntry.CODEC.encodeStart(JsonOps.INSTANCE,e).getOrThrow();assertEquals(e,SpectrobeEntry.CODEC.parse(JsonOps.INSTANCE,json).getOrThrow());
  var d=new PrizmodData();d.addToBox(e);d.setBabySlot(e.withColor(1));
  var saved=PrizmodData.CODEC.encodeStart(JsonOps.INSTANCE,d).getOrThrow();var loaded=PrizmodData.CODEC.parse(JsonOps.INSTANCE,saved).getOrThrow();assertEquals(e,loaded.getBoxAt(0));assertEquals(e.progress(),loaded.getBabySlot().orElseThrow().progress());
 }
 @Test void oldSaveUsesNeutralQualityWithoutLosingStats(){
  var e=entry();var json=SpectrobeEntry.CODEC.encodeStart(JsonOps.INSTANCE,e).getOrThrow().getAsJsonObject();json.remove("progress");var loaded=SpectrobeEntry.CODEC.parse(JsonOps.INSTANCE,json).getOrThrow();assertEquals(e,loaded);assertEquals(-1,loaded.excavationQuality());assertEquals(0,loaded.xp());assertFalse(loaded.crowned());
 }
 @Test void staleTransferAndIncompatibleSwapAreAtomic(){
  var d=new PrizmodData();var child=entry();var adult=entry().withStage("ADULT");d.addToBox(adult);d.setBabySlot(child);
  assertFalse(PrizmodTransfers.move(d,0,0,2,0,adult.id()));assertEquals(adult,d.getBoxAt(0));assertEquals(child,d.getBabySlot().orElseThrow());
  assertFalse(PrizmodTransfers.move(d,0,0,1,0,UUID.randomUUID()));assertEquals(adult,d.getBoxAt(0));assertTrue(d.getTeamSlot(0).isEmpty());
 }
 @Test void boxSwapAndCompanionExchangeLoseNothing(){
  var d=new PrizmodData();var a=entry();var b=entry();var c=entry();d.addToBox(a);d.addToBox(b);d.setBabySlot(c);
  assertTrue(PrizmodTransfers.move(d,0,0,0,1,a.id()));assertEquals(List.of(b,a),d.getBox());
  assertTrue(PrizmodTransfers.move(d,0,1,2,0,a.id()));assertEquals(List.of(b,c),d.getBox());assertEquals(a,d.getBabySlot().orElseThrow());
  assertTrue(PrizmodTransfers.move(d,2,0,0,Integer.MAX_VALUE,a.id()));assertEquals(List.of(b,c,a),d.getBox());assertTrue(d.getBabySlot().isEmpty());
 }
 @Test void automaticExcavationsDoNotCount(){var p=new ExcavationProgress();p.complete("x",0);assertEquals(0,p.completed());p.complete("x",20);assertEquals(1,p.completed());assertEquals(20,p.xp());var copy=ExcavationProgress.CODEC.parse(JsonOps.INSTANCE,ExcavationProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow()).getOrThrow();assertEquals(1,copy.completed());}
 @Test void mineralRanks(){assertEquals(10,SpectrobeLevels.mineralPercent("power_c"));assertEquals(20,SpectrobeLevels.mineralPercent("power_b"));assertEquals(40,SpectrobeLevels.mineralPercent("power_a"));assertEquals(60,SpectrobeLevels.mineralPercent("power_a_plus"));}
 @Test void fullIntegrityAutomaticallyShowsPermanentCrown(){
  var perfect=entry().withProgress(new SpectrobeProgress(0,false,100,0));
  assertTrue(perfect.crowned());assertTrue(perfect.withColor(2).withHpCur(50).crowned());
  assertFalse(entry().withProgress(new SpectrobeProgress(0,false,99,0)).crowned());
  var saved=SpectrobeEntry.CODEC.encodeStart(JsonOps.INSTANCE,perfect).getOrThrow();
  assertTrue(SpectrobeEntry.CODEC.parse(JsonOps.INSTANCE,saved).getOrThrow().crowned());
  assertTrue(SpectrobeLevels.fromFossil(entry(),100,false).crowned());
 }
 @Test void cooperativeBadgeIsCosmeticAndSurvivesTrainingAndSerialization(){
  var original=entry();var marked=original.withProgress(original.progress().withCooperative());
  assertEquals(original.hp(),marked.hp());assertEquals(original.atk(),marked.atk());assertEquals(original.def(),marked.def());
  assertEquals(original.level(),marked.level());assertEquals(original.xp(),marked.xp());assertTrue(marked.cooperative());
  var json=SpectrobeEntry.CODEC.encodeStart(JsonOps.INSTANCE,marked).getOrThrow();
  assertEquals(marked,SpectrobeEntry.CODEC.parse(JsonOps.INSTANCE,json).getOrThrow());
  assertTrue(SpectrobeLevels.award(marked,100,species()).cooperative());
  assertTrue(marked.progress().withCrown().won().cooperative());
  assertFalse(SpectrobeProgress.CODEC.parse(JsonOps.INSTANCE,new com.google.gson.JsonObject()).getOrThrow().cooperative());
 }
 @Test void childCollectionDamageIsRejectedAndOldInjuriesAreRestored(){
  var child=entry().withHpCur(0).withProgress(new SpectrobeProgress(45,true,100,2,true));
  var adult=entry().withStage("ADULT").withHpCur(50);var data=new PrizmodData();data.addToBox(child);data.addToBox(adult);
  assertFalse(data.applyDamage(child.id(),10));assertTrue(data.applyDamage(adult.id(),10));
  data.restoreProtectedChildren();var healed=data.findAnywhere(child.id()).orElseThrow();
  assertEquals(healed.hp(),healed.hpCur());assertEquals(child.progress(),healed.progress());assertEquals(child.atk(),healed.atk());
  assertEquals(40,data.findAnywhere(adult.id()).orElseThrow().hpCur());
 }
}
