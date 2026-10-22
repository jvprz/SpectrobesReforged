package com.jvprz.spectrobesreforged.common.feature.vortex;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class VortexProgressTest {
    private final UUID owner = UUID.randomUUID();
    private final List<UUID> mobs = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

    @Test void onlyThreeDistinctMembersAreAccepted() {
        assertThrows(IllegalArgumentException.class, () -> new VortexProgress(owner, List.of(mobs.get(0), mobs.get(0))));
    }

    @Test void missingOrDuplicateDeathsCannotFinishTheWave() {
        var progress = new VortexProgress(owner, mobs);
        assertFalse(progress.defeated(UUID.randomUUID()));
        assertTrue(progress.defeated(mobs.get(0)));
        assertFalse(progress.defeated(mobs.get(0)));
        progress.advance(20, true);
        assertEquals(2, progress.remaining());
        assertEquals(VortexProgress.Status.ACTIVE, progress.status());
    }

    @Test void victoryRequiresAllDeathsAndNearbyOwnerAndCannotBeRestarted() {
        var progress = new VortexProgress(owner, mobs);
        mobs.forEach(progress::defeated);
        progress.advance(20, false);
        assertEquals(VortexProgress.Status.ACTIVE, progress.status());
        progress.advance(20, true);
        assertEquals(VortexProgress.Status.COMPLETED, progress.status());
        progress.cancel();
        progress.advance(12000, false);
        assertEquals(VortexProgress.Status.COMPLETED, progress.status());
        assertFalse(progress.defeated(mobs.get(0)));
    }

    @Test void absenceCancelsWithoutVictoryAndBriefReturnResetsTheGracePeriod() {
        var progress = new VortexProgress(owner, mobs);
        progress.advance(380, false);
        progress.advance(20, true);
        assertEquals(0, progress.absent());
        progress.advance(400, false);
        mobs.forEach(progress::defeated);
        progress.advance(20, true);
        assertEquals(VortexProgress.Status.CANCELLED, progress.status());
        assertEquals(3, progress.remaining());
    }

    @Test void encounterHasFiniteDurationEvenWithOwnerPresent() {
        var progress = new VortexProgress(owner, mobs);
        progress.advance(12000, true);
        assertEquals(VortexProgress.Status.CANCELLED, progress.status());
    }

    @Test void worldLedgerRoundTripPreservesPartialAndCompletedEncounters() {
        var id = UUID.randomUUID();
        var ledger = new VortexEncounters();
        var progress = new VortexProgress(owner, mobs);
        assertTrue(ledger.begin(id, progress));
        ledger.defeated(id, mobs.get(0));
        progress.advance(80, false);
        var restored = VortexEncounters.load(ledger.save(new CompoundTag(), null), null);
        var saved = restored.find(id);
        assertEquals(owner, saved.owner());
        assertEquals(2, saved.remaining());
        assertEquals(80, saved.absent());
        assertFalse(restored.begin(id, new VortexProgress(owner, mobs)));
        mobs.forEach(saved::defeated);
        saved.advance(20, true);
        var finished = VortexEncounters.load(restored.save(new CompoundTag(), null), null);
        assertEquals(VortexProgress.Status.COMPLETED, finished.find(id).status());
        assertFalse(finished.begin(id, new VortexProgress(owner, mobs)));
    }
    @Test void companionStaysHiddenOnlyForItsOwnersRemainingEncounterEnemies() {
        var ledger = new VortexEncounters();
        var id = UUID.randomUUID();
        ledger.begin(id, new VortexProgress(owner, mobs));
        assertTrue(ledger.hasActiveEnemies(owner));
        assertFalse(ledger.hasActiveEnemies(UUID.randomUUID()));
        ledger.defeated(id, mobs.get(0));
        assertTrue(ledger.hasActiveEnemies(owner));
        var restored = VortexEncounters.load(ledger.save(new CompoundTag(), null), null);
        assertTrue(restored.hasActiveEnemies(owner));
        for (var mob : mobs) restored.defeated(id, mob);
        assertFalse(restored.hasActiveEnemies(owner), "Companion can return after the final enemy dies");
    }

    @Test void cancellationReleasesCompanionButAnotherActiveWaveStillSuppressesIt() {
        var ledger = new VortexEncounters();
        var first = new VortexProgress(owner, mobs);
        var second = new VortexProgress(owner, List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        ledger.begin(UUID.randomUUID(), first);
        ledger.begin(UUID.randomUUID(), second);
        first.cancel();
        assertTrue(ledger.hasActiveEnemies(owner));
        second.cancel();
        assertFalse(ledger.hasActiveEnemies(owner));
    }

}
