package com.jvprz.spectrobesreforged.common.feature.vortex;

import org.junit.jupiter.api.Test;
import net.minecraft.nbt.CompoundTag;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CooperativeRaidTest {
    private List<UUID> wave(){return List.of(UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID());}
    @Test void eachIdentityAddsExactlyThreeEnemiesOnlyOnce(){
        UUID a=UUID.randomUUID(),b=UUID.randomUUID();var raid=new VortexProgress(a,wave());var second=wave();
        assertTrue(raid.join(b,second));assertEquals(6,raid.remaining());
        raid.advance(20,Set.of(a));raid.advance(20,Set.of(a,b));
        assertFalse(raid.join(b,wave()));assertEquals(6,raid.members().size());assertEquals(2,raid.playerCount());
    }
    @Test void duplicateOrOverlappingMembersCannotIncreasePlayerCount(){
        var first=wave();var raid=new VortexProgress(UUID.randomUUID(),first);
        assertFalse(raid.join(UUID.randomUUID(),List.of(first.get(0),UUID.randomUUID(),UUID.randomUUID())));
        assertFalse(raid.join(UUID.randomUUID(),List.of(UUID.randomUUID())));assertEquals(1,raid.playerCount());
    }
    @Test void soloDeathImmediatelyCancelsAndCannotAward(){
        UUID a=UUID.randomUUID();var raid=new VortexProgress(a,wave());assertTrue(raid.died(a));
        assertEquals(VortexProgress.Status.CANCELLED,raid.status());assertFalse(raid.claim(a));assertFalse(raid.join(UUID.randomUUID(),wave()));
    }
    @Test void dyingAndReturningKeepsPenaltyButDoesNotAddAnotherWave(){
        UUID a=UUID.randomUUID(),b=UUID.randomUUID();var raid=new VortexProgress(a,wave());assertTrue(raid.join(b,wave()));
        assertTrue(raid.died(a));assertEquals(VortexProgress.Status.ACTIVE,raid.status());
        raid.advance(1,Set.of(a,b));assertFalse(raid.participant(a).fallen());assertEquals(1,raid.participant(a).deaths());
        assertFalse(raid.join(a,wave()));assertEquals(6,raid.members().size());
        raid.died(a);assertEquals(2,raid.participant(a).deaths());raid.died(b);assertEquals(VortexProgress.Status.CANCELLED,raid.status());
    }
    @Test void allParticipantsMustFallAtTheSameTimeForAWipe(){
        UUID a=UUID.randomUUID(),b=UUID.randomUUID();var raid=new VortexProgress(a,wave());raid.join(b,wave());
        raid.died(a);raid.advance(1,Set.of(a,b));raid.died(b);assertEquals(VortexProgress.Status.ACTIVE,raid.status());
        raid.died(a);assertEquals(VortexProgress.Status.CANCELLED,raid.status());
    }
    @Test void rewardsWaitForTheWholeRaidAndEachParticipantClaimsOnce(){
        UUID a=UUID.randomUUID(),b=UUID.randomUUID();var first=wave();var second=wave();var raid=new VortexProgress(a,first);raid.join(b,second);
        first.forEach(raid::defeated);raid.advance(1,Set.of(a,b));assertFalse(raid.claim(a));
        second.forEach(raid::defeated);raid.advance(1,Set.of(b));assertEquals(VortexProgress.Status.COMPLETED,raid.status());
        assertTrue(raid.claim(a));assertFalse(raid.claim(a));assertTrue(raid.claim(b));assertFalse(raid.claim(UUID.randomUUID()));
    }
    @Test void savedRaidPreservesDeathsClaimsAndAntiReentryMembership(){
        UUID a=UUID.randomUUID(),b=UUID.randomUUID(),id=UUID.randomUUID();var raid=new VortexProgress(a,wave());raid.join(b,wave());raid.died(a);
        var ledger=new VortexEncounters();ledger.begin(id,raid);
        var restored=VortexEncounters.load(ledger.save(new CompoundTag(),null),null).find(id);
        assertEquals(6,restored.members().size());assertEquals(1,restored.participant(a).deaths());assertFalse(restored.join(a,wave()));
        restored.members().forEach(restored::defeated);restored.advance(1,Set.of(b));assertTrue(restored.claim(b));
        var complete=new VortexEncounters();complete.begin(id,restored);
        var saved=VortexEncounters.load(complete.save(new CompoundTag(),null),null).find(id);
        assertFalse(saved.claim(b));assertTrue(saved.claim(a));
    }
    @Test void rewardsScaleWithACapAndEveryDeathMakesThemWorse(){
        assertEquals(new VortexRewards.Budget(2,0,15,60),VortexRewards.budget(1,0));
        assertEquals(new VortexRewards.Budget(3,0,20,60),VortexRewards.budget(2,0));
        assertEquals(VortexRewards.budget(4,0),VortexRewards.budget(16,0));
        for(int size=2;size<=16;size++)for(int deaths=1;deaths<=3;deaths++){
            var reward=VortexRewards.budget(size,deaths);var full=VortexRewards.budget(size,0);
            assertTrue(reward.c()+reward.b()<full.c()+full.b());assertTrue(reward.xp()<full.xp());assertEquals(0,reward.b());
        }
    }
    @Test void companionsAreSuppressedForEveryRegisteredParticipant(){
        UUID a=UUID.randomUUID(),b=UUID.randomUUID();var raid=new VortexProgress(a,wave());raid.join(b,wave());var ledger=new VortexEncounters();ledger.begin(UUID.randomUUID(),raid);
        assertTrue(ledger.hasActiveEnemies(a));assertTrue(ledger.hasActiveEnemies(b));raid.cancel();assertFalse(ledger.hasActiveEnemies(b));
    }
}
