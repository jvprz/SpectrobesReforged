package com.jvprz.spectrobesreforged.common.feature.excavation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExcavationGeologyTest {
    private int center(){return 16*32+16;}
    @Test void rawGradeChangesCoverThicknessOpeningAndRockDepth() {
        var basic=ExcavationProfile.forSpecimen("power_c",ExcavationPuzzle.ICE,0);
        var rare=ExcavationProfile.forSpecimen("power_a_plus",ExcavationPuzzle.ICE,0);
        assertTrue(rare.coverLayers()>basic.coverLayers());assertTrue(rare.openingLayers()>basic.openingLayers());
        assertTrue(rare.minDepth()>basic.minDepth());
        var deep=ExcavationProfile.forSpecimen("power_c",ExcavationPuzzle.ROCK,ExcavationProfile.DEEPSLATE);
        assertEquals(0,deep.coverLayers());assertEquals(3,deep.minDepth());assertEquals(4,deep.maxDepth());
    }
    @Test void thickIceRequiresThreeMatchingActionsAndCannotBeDrilledOrBlownAway() {
        var p=new ExcavationPuzzle(ExcavationMasks.get("power_a_plus"),42,ExcavationPuzzle.ICE,new ExcavationProfile(0,3,0,3,4));
        assertEquals(3,ExcavationPuzzle.coverLayers(p.cell(center())));
        assertFalse(p.work(16,16,ExcavationPuzzle.DRILL));assertFalse(p.work(16,16,ExcavationPuzzle.BLOWER));
        for(int remaining=3;remaining>0;remaining--){assertEquals(remaining,ExcavationPuzzle.coverLayers(p.cell(center())));assertTrue(p.work(16,16,ExcavationPuzzle.FIRE));}
        assertEquals(0,ExcavationPuzzle.coverLayers(p.cell(center())));assertTrue((p.cell(center())&7)>=3);
    }
    @Test void signedCoverBytesAndRockSurviveSaveAndRestore() {
        var profile=new ExcavationProfile(ExcavationProfile.CLAY,3,4,3,4);
        var p=new ExcavationPuzzle(ExcavationMasks.get("komainu"),27,ExcavationPuzzle.MUD,profile);
        assertTrue(p.snapshot()[center()]<0);
        p.work(16,16,ExcavationPuzzle.TAP);
        var restored=ExcavationPuzzle.restore(p.state());assertEquals(3,restored.overburden());assertEquals(ExcavationProfile.CLAY,restored.rock());
        assertArrayEquals(p.snapshot(),restored.snapshot());assertEquals(3,ExcavationPuzzle.coverLayers(restored.cell(center())));
    }
    @Test void oldPrototypeSavesStillHaveOneCoverAndDefaultStone() {
        var p=new ExcavationPuzzle(ExcavationMasks.get("shakin"),3,ExcavationPuzzle.ICE,0);
        var old=new ExcavationPuzzle.State(p.snapshot(),100,200,false,0,0,0,0,0,2,false,7,0);
        var restored=ExcavationPuzzle.restore(old);assertEquals(0,restored.rock());assertEquals(1,ExcavationPuzzle.coverLayers(restored.cell(center())));
        restored.work(16,16,ExcavationPuzzle.FIRE);assertEquals(0,ExcavationPuzzle.coverLayers(restored.cell(center())));
    }
    @Test void everyEnvironmentalGradeRemainsRecoverableWithoutDamage() {
        for(var deposit:ExcavationDeposits.all()) {
            String key=deposit.fossil()?switch(deposit.element()){case "corona"->"komainu";case "aurora"->"segu";default->"shakin";}:"power_"+deposit.grade();
            var p=new ExcavationPuzzle(ExcavationMasks.get(key),24,deposit.terrain(),ExcavationProfile.forSpecimen(key,deposit.terrain(),deposit.rock()));
            while(p.overburden()>0)p.work(16,16,ExcavationPuzzle.TAP);
            int clear=switch(deposit.terrain()){case 1->ExcavationPuzzle.BLOWER;case 2->ExcavationPuzzle.FIRE;default->ExcavationPuzzle.VACUUM;};
            for(int pass=0;pass<7;pass++)for(int y=0;y<32;y+=3)for(int x=0;x<32;x+=3){p.work(x,y,clear);p.work(x,y,ExcavationPuzzle.BLOWER);p.work(x,y,ExcavationPuzzle.DRILL);p.work(x,y,ExcavationPuzzle.BLOWER);}
            assertTrue(p.canExtract(),deposit.id());assertEquals(100,p.integrity(),deposit.id());
        }
    }
    @Test void gigaClearsAllNewCoverBitsAndPreservesTheHost() {
        var p=new ExcavationPuzzle(ExcavationMasks.get("power_a_plus"),3,ExcavationPuzzle.MUD,new ExcavationProfile(3,3,0,3,4));
        assertTrue(p.work(16,16,ExcavationPuzzle.GIGA,1,true));assertEquals(100,p.percent());assertEquals(3,p.rock());
        for(byte cell:p.snapshot())assertEquals(0,(cell&255)&~ExcavationPuzzle.FOSSIL);
        assertEquals(0,p.experience());
    }
 @Test void redSandstoneSurvivesExcavationSaveAndUsesRedMaterials(){
  var profile=ExcavationProfile.forSpecimen("komainu",ExcavationPuzzle.SAND,ExcavationProfile.RED_SANDSTONE);
  var puzzle=new ExcavationPuzzle(ExcavationMasks.get("komainu"),42,ExcavationPuzzle.SAND,profile);
  assertEquals(4,ExcavationPuzzle.restore(puzzle.state()).rock());
  assertEquals("red_sandstone",ExcavationProfile.rockTexture(4));
  assertEquals("red_sandstone_top",ExcavationProfile.openingTexture(3,ExcavationPuzzle.SAND,4));
  assertEquals("red_sand",ExcavationProfile.openingTexture(1,ExcavationPuzzle.SAND,4));
 }
}
