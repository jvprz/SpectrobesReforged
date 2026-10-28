package com.jvprz.spectrobesreforged.common.feature.excavation;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DSExcavationTest {
    private boolean[] mask() { boolean[] m=new boolean[1024];for(int y=10;y<22;y++)for(int x=10;x<22;x++)m[y*32+x]=true;return m; }
    private ExcavationPuzzle exposed(int health, boolean damaged) {
        byte[] cells=new byte[1024];cells[16*32+16]=ExcavationPuzzle.FOSSIL;
        return ExcavationPuzzle.restore(cells,health,0,damaged);
    }
    @Test void openingRequiresDistinctTapsAndCannotBeBypassedByTools() {
        var p=new ExcavationPuzzle(mask(),3,0,3);
        assertFalse(p.work(16,16,ExcavationPuzzle.DRILL));assertFalse(p.work(16,16,ExcavationPuzzle.GIGA,1,true));
        assertEquals(0,p.percent());
        for(int i=3;i>0;i--){assertEquals(i,p.overburden());assertTrue(p.work(16,16,ExcavationPuzzle.TAP));}
        assertEquals(0,p.overburden());assertTrue(p.work(16,16,ExcavationPuzzle.DRILL));
    }
    @Test void everySpecialTerrainNeedsItsMatchingToolBeforeRockCanBeWorked() {
        int[] terrains={1,2,3,4,5},tools={ExcavationPuzzle.BLOWER,ExcavationPuzzle.FIRE,ExcavationPuzzle.WATER,ExcavationPuzzle.VACUUM,ExcavationPuzzle.VACUUM};
        for(int i=0;i<terrains.length;i++){
            var p=new ExcavationPuzzle(mask(),8,terrains[i],0);
            int before=p.cell(16*32+16);assertFalse(p.work(16,16,ExcavationPuzzle.DRILL));assertEquals(before,p.cell(16*32+16));
            assertTrue(p.work(16,16,tools[i]));assertEquals(0,p.cell(16*32+16)&ExcavationPuzzle.COVER);
            assertTrue(p.work(16,16,ExcavationPuzzle.DRILL));
        }
    }
    @Test void releasingTheDrillCoolsStressAndBlowerClearsHeatAndGas() {
        var p=exposed(100,false);for(int i=0;i<8;i++)p.work(16,16,ExcavationPuzzle.DRILL);
        assertTrue(p.stress()>0);assertEquals(100,p.integrity());
        for(int i=0;i<60;i++)p.tick();assertEquals(0,p.stress());
        p=new ExcavationPuzzle(mask(),8,ExcavationPuzzle.ICE,0);p.work(16,16,ExcavationPuzzle.FIRE);
        assertTrue(p.heat()>0);p.work(16,16,ExcavationPuzzle.BLOWER);assertEquals(0,p.heat());
        p.work(16,16,ExcavationPuzzle.DRILL);assertTrue(p.gas()>0);p.work(16,16,ExcavationPuzzle.BLOWER);assertEquals(0,p.gas());
    }
    @Test void recoveryIsLimitedSavedAndDoesNotRestoreAPerfectAttempt() {
        var p=exposed(45,true);
        for(int i=0;i<4;i++)assertTrue(p.work(16,16,ExcavationPuzzle.RECOVERY));
        assertEquals(85,p.integrity());assertEquals(0,p.repairsLeft());assertFalse(p.work(16,16,ExcavationPuzzle.RECOVERY));
        var restored=ExcavationPuzzle.restore(p.state());assertEquals(0,restored.repairsLeft());assertEquals(85,restored.integrity());
        assertFalse(restored.crowned());assertFalse(restored.work(16,16,ExcavationPuzzle.RECOVERY));
        p=exposed(90,true);p.work(16,16,ExcavationPuzzle.RECOVERY);assertEquals(100,p.integrity());assertFalse(p.crowned());
    }
    @Test void automaticToolsNeverGrantSkillXpOrCrownsAndGigaRequiresCatalogue() {
        var p=new ExcavationPuzzle(mask(),2);
        assertFalse(p.work(16,16,ExcavationPuzzle.GIGA,1,false));assertFalse(p.automatic());
        assertTrue(p.work(16,16,ExcavationPuzzle.GIGA,1,true));assertTrue(p.canExtract());
        assertEquals(100,p.percent());assertTrue(p.automatic());assertEquals(0,p.experience());assertFalse(p.crowned());
        assertFalse(p.work(16,16,ExcavationPuzzle.GIGA,1,true));
    }
    @Test void blastHasBothOutcomesAndCannotRerollAfterSaving() {
        int success=0,failed=0;
        for(int seed=0;seed<100;seed++){
            var p=new ExcavationPuzzle(mask(),seed);assertTrue(p.work(16,16,ExcavationPuzzle.BLAST));
            if(p.canExtract())success++;else{failed++;assertEquals(0,p.integrity());}
            var restored=ExcavationPuzzle.restore(p.state());assertEquals(p.integrity(),restored.integrity());assertTrue(restored.automatic());
            assertFalse(restored.work(16,16,ExcavationPuzzle.BLAST));assertEquals(0,restored.experience());
        }
        assertTrue(success>30&&success<70);assertEquals(100,success+failed);
    }
    @Test void specialToolsAndSkillRemainPlayableAcrossEverySpeciesAndTerrain() {
        int[] clearing={ExcavationPuzzle.BLOWER,ExcavationPuzzle.BLOWER,ExcavationPuzzle.FIRE,ExcavationPuzzle.WATER,ExcavationPuzzle.VACUUM,ExcavationPuzzle.VACUUM};
        for(String name:new String[]{"komainu","inkana","vilar","segu","spiko","shakin","power_c","health_a_plus"})for(int terrain=0;terrain<6;terrain++){
            var p=new ExcavationPuzzle(ExcavationMasks.get(name),42,terrain,0);
            for(int pass=0;pass<4;pass++)for(int y=0;y<32;y+=3)for(int x=0;x<32;x+=3){
                p.work(x,y,clearing[terrain]);p.work(x,y,ExcavationPuzzle.BLOWER);
                p.work(x,y,ExcavationPuzzle.DRILL);p.work(x,y,ExcavationPuzzle.BLOWER);
            }
            assertTrue(p.canExtract(),name+"/"+terrain);assertEquals(100,p.integrity(),name+"/"+terrain);
        }
    }
    @Test void stateRoundTripPreservesAllGameplayAndReturnsDefensiveCopies() {
        var p=new ExcavationPuzzle(mask(),123,ExcavationPuzzle.ICE,2);p.work(16,16,14);
        var q=ExcavationPuzzle.restore(p.state());assertArrayEquals(p.snapshot(),q.snapshot());assertEquals(1,q.overburden());assertEquals(2,q.terrain());
        q.work(16,16,14);q.work(16,16,ExcavationPuzzle.FIRE);for(int i=0;i<40;i++)q.tick();
        var saved=q.state();var restored=ExcavationPuzzle.restore(saved);
        assertEquals(q.heat(),restored.heat());assertEquals(40,restored.elapsedTicks());assertArrayEquals(q.snapshot(),restored.snapshot());
        byte[] copy=saved.cells();copy[0]=0;assertNotEquals(0,saved.cells()[0]);
    }
    @Test void catalogueAndSkillSurviveSerializationAndAutomationCannotTrainThem() {
        var p=new ExcavationProgress();assertEquals(1,p.level());assertFalse(p.knows("komainu_fossil"));
        p.complete("komainu_fossil",0);assertFalse(p.knows("komainu_fossil"));
        p.complete("komainu_fossil",275);assertEquals(2,p.level());assertEquals(25,p.progress());
        var crowned=java.util.UUID.randomUUID();p.rememberCrown(crowned);
        var json=ExcavationProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow();
        var q=ExcavationProgress.CODEC.parse(JsonOps.INSTANCE,json).getOrThrow();assertEquals(275,q.xp());assertTrue(q.knows("komainu_fossil"));assertTrue(q.hasCrown(crowned));
        q.complete("power_c",1000000);assertEquals(10,q.level());assertEquals(250,q.progress());
    }
    @Test void superBlowerClearsTheWholeBoardEvenWhenAimedAtACorner() {
        byte[] cells=new byte[1024];for(int i=0;i<cells.length;i++)cells[i]=(byte)(ExcavationPuzzle.FOSSIL|1|ExcavationPuzzle.DUST);
        var p=ExcavationPuzzle.restore(cells,100,0,false);
        p.work(0,0,ExcavationPuzzle.BLOWER_S);assertEquals(100,p.percent());assertEquals(100,p.integrity());
    }
    @Test void fineHeavySolventAndSonicHaveDifferentSpeedAndSafety() {
        byte[] cells=new byte[1024];cells[16*32+16]=(byte)(ExcavationPuzzle.FOSSIL|4);
        var fine=ExcavationPuzzle.restore(cells,100,0,false);fine.work(16,16,ExcavationPuzzle.FINE);assertEquals(3,fine.cell(16*32+16)&7);
        var heavy=ExcavationPuzzle.restore(cells,100,0,false);heavy.work(16,16,ExcavationPuzzle.HEAVY);assertEquals(2,heavy.cell(16*32+16)&7);
        var solvent=ExcavationPuzzle.restore(cells,100,0,false);solvent.work(16,16,ExcavationPuzzle.SOLVENT);assertEquals(4,solvent.cell(16*32+16)&7);
        solvent.work(16,16,ExcavationPuzzle.SOLVENT);assertEquals(3,solvent.cell(16*32+16)&7);
        var sonic=ExcavationPuzzle.restore(cells,100,0,false);
        for(int i=0;i<2;i++)sonic.work(16,16,ExcavationPuzzle.SONIC);assertEquals(4,sonic.cell(16*32+16)&7);
        sonic.work(16,16,ExcavationPuzzle.SONIC);assertEquals(3,sonic.cell(16*32+16)&7);
        for(int i=0;i<300;i++)sonic.work(16,16,ExcavationPuzzle.SONIC);assertEquals(100,sonic.integrity());
    }
    @Test void accumulatedGasStopsDrillingUntilItIsCleared() {
        var p=new ExcavationPuzzle(mask(),32);
        for(int y=0;y<32;y+=3)for(int x=0;x<32;x+=3)p.work(x,y,ExcavationPuzzle.DRILL);
        assertTrue(p.gas()>=85);int before=p.cell(31*32+31)&7;
        p.work(31,31,ExcavationPuzzle.DRILL);assertEquals(before,p.cell(31*32+31)&7);
        p.work(31,31,ExcavationPuzzle.BLOWER);assertTrue(p.gas()<85);
        p.work(31,31,ExcavationPuzzle.DRILL);assertTrue((p.cell(31*32+31)&7)<before);
    }
    @Test void higherSkillReducesDamageOverTheSameSavedRandomStreams() {
        int beginners=0,experts=0;
        for(int seed=0;seed<100;seed++){
            byte[] cells=new byte[1024];cells[16*32+16]=ExcavationPuzzle.FOSSIL;
            var state=new ExcavationPuzzle.State(cells,100,0,false,0,0,0,0,0,0,false,seed,0);
            var low=ExcavationPuzzle.restore(state);var high=ExcavationPuzzle.restore(state);
            for(int i=0;i<60;i++){low.work(16,16,ExcavationPuzzle.DRILL,1,false);high.work(16,16,ExcavationPuzzle.DRILL,10,false);}
            beginners+=100-low.integrity();experts+=100-high.integrity();
        }
        assertTrue(experts<beginners);assertTrue(experts>0);
    }
    @Test void manuallyDestroyedSpecimenCannotBeRepairedOrRecoveredAfterSaving() {
        var p=exposed(2,true);
        for(int i=0;i<20&&p.integrity()>0;i++)p.work(16,16,ExcavationPuzzle.DRILL);
        assertEquals(0,p.integrity());
        var restored=ExcavationPuzzle.restore(p.state());
        assertFalse(restored.canExtract());assertEquals(0,restored.experience());
        for(int tool=0;tool<=ExcavationPuzzle.TAP;tool++)assertFalse(restored.work(16,16,tool,10,true));
        assertEquals(0,restored.integrity());assertFalse(restored.crowned());
    }
    @Test void malformedCoverFlagsAndOldSavesAreHandledWithoutLosingProgress() {
        byte[] old=new byte[1024];old[0]=ExcavationPuzzle.FOSSIL;
        var p=ExcavationPuzzle.restore(old,80,1400,true);assertTrue(p.canExtract());assertEquals(0,p.overburden());assertEquals(4,p.repairsLeft());
        old[1]=64;assertThrows(IllegalArgumentException.class,()->ExcavationPuzzle.restore(old,100,0,false));
    }
}
