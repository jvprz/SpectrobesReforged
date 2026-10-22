package com.jvprz.spectrobesreforged.common.feature.vortex;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Persistent, finite encounter state. Only confirmed deaths count as a victory. */
public final class VortexProgress {
    public enum Status { ACTIVE, COMPLETED, CANCELLED }
    public static final int ABSENCE_LIMIT = 400;
    public static final int DURATION_LIMIT = 12000;
    private final UUID owner;
    private final Set<UUID> members;
    private final Set<UUID> defeated = new HashSet<>();
    private Status status = Status.ACTIVE;
    private int elapsed;
    private int absent;

    public VortexProgress(UUID owner, Collection<UUID> members) {
        this.owner = java.util.Objects.requireNonNull(owner);
        this.members = Set.copyOf(members);
        if (this.members.size() != 3) throw new IllegalArgumentException("A small vortex needs three distinct Krawl");
    }

    public UUID owner() { return owner; }
    public Set<UUID> members() { return members; }
    public Set<UUID> defeated() { return Set.copyOf(defeated); }
    public Status status() { return status; }
    public int elapsed() { return elapsed; }
    public int absent() { return absent; }
    public int remaining() { return members.size() - defeated.size(); }

    public boolean defeated(UUID entity) {
        return status == Status.ACTIVE && members.contains(entity) && defeated.add(entity);
    }

    public void advance(int ticks, boolean ownerNearby) {
        if (status != Status.ACTIVE || ticks <= 0) return;
        elapsed = Math.min(DURATION_LIMIT, elapsed + Math.min(ticks, DURATION_LIMIT));
        absent = ownerNearby ? 0 : Math.min(ABSENCE_LIMIT, absent + Math.min(ticks, ABSENCE_LIMIT));
        if (absent >= ABSENCE_LIMIT || elapsed >= DURATION_LIMIT) status = Status.CANCELLED;
        else if (ownerNearby && remaining() == 0) status = Status.COMPLETED;
    }

    public void cancel() { if (status == Status.ACTIVE) status = Status.CANCELLED; }

    void restore(Status status, Collection<UUID> defeated, int elapsed, int absent) {
        this.status = status;
        this.defeated.addAll(defeated);
        this.defeated.retainAll(members);
        this.elapsed = Math.max(0, Math.min(DURATION_LIMIT, elapsed));
        this.absent = Math.max(0, Math.min(ABSENCE_LIMIT, absent));
    }
}
