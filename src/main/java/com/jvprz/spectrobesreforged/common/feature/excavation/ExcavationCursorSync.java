package com.jvprz.spectrobesreforged.common.feature.excavation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Per-open-menu delivery state. A new viewer always receives the current cursors. */
public final class ExcavationCursorSync {
    private record Delivery(int container, List<ExcavationCoop.Cursor> cursors) {}
    private final Map<UUID, Delivery> delivered = new HashMap<>();

    public boolean shouldSend(UUID player, int container, List<ExcavationCoop.Cursor> cursors) {
        var next = new Delivery(container, List.copyOf(cursors));
        return !next.equals(delivered.put(player, next));
    }

    public void forget(UUID player) { delivered.remove(player); }
    public void clear() { delivered.clear(); }
}
