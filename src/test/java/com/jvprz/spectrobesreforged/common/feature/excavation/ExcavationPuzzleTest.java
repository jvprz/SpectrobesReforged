package com.jvprz.spectrobesreforged.common.feature.excavation;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class ExcavationPuzzleTest {
    private boolean[] mask() { boolean[] result = new boolean[1024]; for (int i = 0; i < 10; i++) result[i] = true; return result; }
    private ExcavationPuzzle revealed(int uncovered, int health, int ticks, boolean damaged) {
        byte[] cells = new byte[1024];
        for (int i = 0; i < 10; i++) cells[i] = (byte) (ExcavationPuzzle.FOSSIL | (i < uncovered ? 0 : ExcavationPuzzle.DUST));
        return ExcavationPuzzle.restore(cells, health, ticks, damaged);
    }
    @Test void seededLayoutIsStableAndSnapshotsCannotMutateIt() {
        var a = new ExcavationPuzzle(mask(), 123); var b = new ExcavationPuzzle(mask(), 123);
        assertArrayEquals(a.snapshot(), b.snapshot());
        byte[] copy = a.snapshot(); copy[0] = 0; assertNotEquals(0, a.cell(0));
    }
    @Test void canExtractAtNinetyButNotBeforeOrWhenDestroyed() {
        assertFalse(revealed(8, 100, 0, false).canExtract());
        assertTrue(revealed(9, 100, 0, false).canExtract());
        assertFalse(revealed(10, 0, 0, false).canExtract());
    }
    @Test void crownRequiresCompleteUnharmedExcavationWithinSixtySeconds() {
        assertTrue(revealed(10, 100, 1200, false).crowned());
        assertFalse(revealed(10, 100, 1201, false).crowned());
        assertFalse(revealed(9, 100, 100, false).crowned());
        assertFalse(revealed(10, 100, 100, true).crowned());
    }
    @Test void blowerClearsDustButCannotDrillRockOrHarmFossil() {
        var p = new ExcavationPuzzle(mask(), 4); byte[] before = p.snapshot();
        assertFalse(p.work(0, 0, ExcavationPuzzle.BLOWER)); assertArrayEquals(before, p.snapshot());
        p = revealed(9, 100, 0, false); assertTrue(p.work(9, 0, ExcavationPuzzle.BLOWER));
        assertEquals(100, p.percent()); assertEquals(100, p.integrity());
    }
    @Test void drillRemovesLayersAndLeavesDebrisBeforeFossilCountsAsExposed() {
        boolean[] mask = new boolean[1024]; mask[16*32+16] = true;
        var p = new ExcavationPuzzle(mask, 1);
        while ((p.cell(16*32+16) & 7) > 0) assertTrue(p.work(16,16,0));
        assertEquals(100,p.integrity()); assertEquals(0,p.percent());
        assertTrue(p.work(16,16,1)); assertEquals(100,p.percent());
    }
    @Test void drillingExposedFossilDamagesAndEventuallyDestroysIt() {
        var p = revealed(10,100,0,false);
        for (int i=0;i<220;i++) p.work(0,0,0);
        assertEquals(0,p.integrity()); assertFalse(p.canExtract()); assertFalse(p.work(0,0,1));
    }
    @Test void blowerSafelyRemovesTheFinalThinLayer() {
        byte[] cells = new byte[1024]; cells[16*32+16] = (byte)(ExcavationPuzzle.FOSSIL | 1);
        var p = ExcavationPuzzle.restore(cells,100,0,false);
        assertTrue(p.work(16,16,ExcavationPuzzle.BLOWER));
        assertEquals(100,p.percent()); assertEquals(100,p.integrity()); assertTrue(p.crowned());
    }
    @Test void accidentalContactHasGraceAndDoesNotBlockNearbyRock() {
        byte[] cells = new byte[1024]; cells[16*32+16] = ExcavationPuzzle.FOSSIL;
        cells[16*32+17] = 3;
        var p = ExcavationPuzzle.restore(cells,100,0,false);
        assertTrue(p.work(16,16,0)); assertEquals(2,p.cell(16*32+17)&7);
        assertEquals(100,p.integrity());
        p.work(16,16,0); p.work(16,16,0);
        for(int i=0;i<11;i++) p.work(16,16,0);
        assertEquals(100,p.integrity());
        p.work(16,16,0); assertEquals(98,p.integrity());
    }
    @Test void sweepingDrillAndBlowerCanRecoverEveryPlayableFossilUnharmed() {
        for(String species:new String[]{"komainu","inkana","vilar","segu","spiko","shakin"}) {
            var p=new ExcavationPuzzle(ExcavationMasks.get(species),42);
            for(int pass=0;pass<4;pass++) for(int y=0;y<32;y+=3) for(int x=0;x<32;x+=3) {
                p.work(x,y,0); p.work(x,y,1);
            }
            assertTrue(p.canExtract(),species); assertEquals(100,p.integrity(),species);
        }
    }
    @Test void closingAndRestoringPreservesGridDamageAndTime() {
        var p = new ExcavationPuzzle(mask(),19); p.work(0,0,0);
        for(int i=0;i<300;i++)p.tick();
        var restored=ExcavationPuzzle.restore(p.snapshot(),p.integrity(),p.elapsedTicks(),p.damaged());
        assertArrayEquals(p.snapshot(),restored.snapshot()); assertEquals(300,restored.elapsedTicks());
        assertEquals(p.integrity(),restored.integrity()); assertEquals(p.percent(),restored.percent());
    }
    @Test void invalidActionsAndMalformedSavedGridsAreRejected() {
        var p = new ExcavationPuzzle(mask(),1); byte[] before=p.snapshot();
        assertFalse(p.work(-1,0,0)); assertFalse(p.work(32,0,0)); assertFalse(p.work(0,32,0)); assertFalse(p.work(0,0,99));
        assertArrayEquals(before,p.snapshot());
        assertThrows(IllegalArgumentException.class,()->ExcavationPuzzle.restore(new byte[2],100,0,false));
        assertThrows(IllegalArgumentException.class,()->new ExcavationPuzzle(new boolean[1024],1));
    }
    @Test void everyPlayableSpeciesHasANonEmptySilhouette() {
        for (String species : new String[]{"komainu","inkana","vilar","segu","spiko","shakin"}) {
            boolean[] mask=ExcavationMasks.get(species); assertNotNull(mask); assertEquals(1024,mask.length);
            var puzzle=new ExcavationPuzzle(mask,4); assertEquals(0,puzzle.percent());
        }
    }
}
