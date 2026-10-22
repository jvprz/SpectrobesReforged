package com.jvprz.spectrobesreforged.client.feature.scanner;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class FossilScanTest {
    @Test
    void emptySearchIsBoundedPerTickAndEventuallyCompletesTheCylinder() {
        var scan = new FossilScan();
        var visited = new HashSet<BlockPos>();
        var count = new AtomicInteger();
        boolean finished = false;
        for (int tick = 0; tick < 100 && !finished; tick++) {
            count.set(0);
            finished = scan.tick(tick, new BlockPos(0, 80, 0), 10, 64, 12, -64, 320,
                    pos -> true, pos -> {
                        count.incrementAndGet();
                        assertTrue(visited.add(pos.immutable()), "Position searched twice");
                        return false;
                    });
            assertTrue(count.get() <= 2048, "Exceeded tick budget");
            if (tick == 0) assertFalse(finished);
        }
        int columns = 0;
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                if (x * x + z * z <= 100) columns++;
            }
        }
        assertTrue(finished);
        assertEquals(columns * 65, visited.size());
        assertTrue(scan.results().isEmpty());
    }

    @Test
    void resultsAreLimitedAndPositionsDoNotAliasTheMutableCursor() {
        var scan = new FossilScan();
        boolean finished = scan.tick(0, new BlockPos(0, 80, 0), 3, 64, 12, -64, 320,
                pos -> true, pos -> true);
        assertTrue(finished);
        assertEquals(12, scan.results().size());
        assertEquals(12, new HashSet<>(scan.results()).size());
        assertThrows(UnsupportedOperationException.class, () -> scan.results().clear());
    }

    @Test
    void unloadedBlocksAreSkippedAndFinishedSearchWaitsBeforeRescanning() {
        var scan = new FossilScan();
        assertTrue(scan.tick(0, new BlockPos(0, 80, 0), 1, 0, 12, -64, 320,
                pos -> false, pos -> { fail("Read from an unloaded chunk"); return false; }));
        assertTrue(scan.results().isEmpty());
        assertFalse(scan.tick(9, new BlockPos(0, 80, 0), 1, 0, 12, -64, 320,
                pos -> true, pos -> true));
        assertTrue(scan.tick(10, new BlockPos(0, 80, 0), 1, 0, 12, -64, 320,
                pos -> true, pos -> true));
        assertEquals(5, scan.results().size());
    }
}
