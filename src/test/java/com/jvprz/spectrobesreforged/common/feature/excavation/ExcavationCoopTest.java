package com.jvprz.spectrobesreforged.common.feature.excavation;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ExcavationCoopTest {
    @Test void sameToolIsExclusiveButDifferentToolsCanWorkTogether(){
        var c=new ExcavationCoop();UUID a=UUID.randomUUID(),b=UUID.randomUUID();c.add(a,"A");c.add(b,"B");
        assertTrue(c.stroke(a,ExcavationPuzzle.DRILL,0));assertFalse(c.stroke(b,ExcavationPuzzle.DRILL,0));assertTrue(c.stroke(b,ExcavationPuzzle.BLOWER,0));
        assertFalse(c.stroke(a,ExcavationPuzzle.DRILL,1));assertTrue(c.stroke(a,ExcavationPuzzle.DRILL,2));
    }
    @Test void releaseDisconnectAndExpiredLeasesCannotLeaveToolsStuck(){
        var c=new ExcavationCoop();UUID a=UUID.randomUUID(),b=UUID.randomUUID();c.add(a,"A");c.add(b,"B");
        c.acquire(a,0,0);c.touch(a,50,50,0,false,1);assertTrue(c.acquire(b,0,1));
        c.leave(b);assertTrue(c.acquire(a,0,2));assertNull(c.owner(0,12));assertTrue(c.acquire(a,1,13));assertNull(c.owner(0,13));
    }
    @Test void spoofedViewersAndInvalidToolsCannotReserveAnything(){
        var c=new ExcavationCoop();UUID a=UUID.randomUUID();assertFalse(c.acquire(a,0,0));c.add(a,"A");assertFalse(c.acquire(a,999,0));
        c.touch(a,-1,-1,0,true,0);assertNull(c.owner(0,0));assertFalse(c.snapshot(0).getFirst().using());
    }
}
