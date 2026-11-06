package com.jvprz.spectrobesreforged.common.feature.excavation;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExcavationCursorSyncTest {
    @Test void stationaryCursorsSendOnceButNewMenusAndViewersReceiveTheirState() {
        var sync=new ExcavationCursorSync();
        var a=UUID.randomUUID();var b=UUID.randomUUID();
        var state=List.of(new ExcavationCoop.Cursor(a,"A",20,30,0,false));
        assertTrue(sync.shouldSend(a,1,state));
        assertFalse(sync.shouldSend(a,1,state));
        assertTrue(sync.shouldSend(b,1,state));
        assertTrue(sync.shouldSend(a,2,state));
        sync.forget(a);
        assertTrue(sync.shouldSend(a,2,state));
        sync.clear();
        assertTrue(sync.shouldSend(b,1,state));
    }
    @Test void MovementDepartureAndExpiredToolLeaseAreDelivered() {
        var coop=new ExcavationCoop();var sync=new ExcavationCursorSync();var a=UUID.randomUUID();
        coop.add(a,"A");coop.touch(a,20,30,0,true,0);
        assertTrue(sync.shouldSend(a,1,coop.snapshot(0)));
        assertFalse(sync.shouldSend(a,1,coop.snapshot(4)));
        assertTrue(sync.shouldSend(a,1,coop.snapshot(10)),"Lease expiry must release the visible tool");
        coop.touch(a,21,30,0,false,11);
        assertTrue(sync.shouldSend(a,1,coop.snapshot(11)));
        coop.leave(a);
        assertTrue(sync.shouldSend(a,1,coop.snapshot(12)),"Departure must clear the remote cursor");
    }
}
