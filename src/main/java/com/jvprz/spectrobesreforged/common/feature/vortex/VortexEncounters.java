package com.jvprz.spectrobesreforged.common.feature.vortex;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** World-level ledger survives chunk unload and remembers terminal encounters. */
public final class VortexEncounters extends SavedData {
    private static final Factory<VortexEncounters> FACTORY =
            new Factory<>(VortexEncounters::new, VortexEncounters::load, null);
    private final Map<UUID, VortexProgress> encounters = new HashMap<>();

    public static VortexEncounters get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, "spectrobesreforged_vortices");
    }

    public VortexProgress find(UUID id) { return encounters.get(id); }

    public boolean hasActiveEnemies(UUID owner) {
        return encounters.values().stream().anyMatch(encounter -> encounter.owner().equals(owner)
                && encounter.status() == VortexProgress.Status.ACTIVE && encounter.remaining() > 0);
    }

    /** Check every dimension so changing dimension cannot prematurely release the companion. */
    public static boolean companionSuppressed(net.minecraft.server.level.ServerPlayer player) {
        for (ServerLevel level : player.server.getAllLevels()) {
            if (get(level).hasActiveEnemies(player.getUUID())) return true;
        }
        return false;
    }

    public boolean begin(UUID id, VortexProgress progress) {
        if (encounters.putIfAbsent(id, progress) != null) return false;
        setDirty();
        return true;
    }

    public void defeated(UUID id, UUID mob) {
        var progress = find(id);
        if (progress != null && progress.defeated(mob)) setDirty();
    }

    static VortexEncounters load(CompoundTag tag, HolderLookup.Provider registries) {
        var data = new VortexEncounters();
        var entries = tag.getList("Encounters", Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.getCompound(i);
            try {
                var members = new ArrayList<UUID>();
                var defeated = new ArrayList<UUID>();
                var mobs = entry.getList("Members", Tag.TAG_COMPOUND);
                for (int j = 0; j < mobs.size(); j++) {
                    var mob = mobs.getCompound(j);
                    UUID id = mob.getUUID("Id");
                    members.add(id);
                    if (mob.getBoolean("Defeated")) defeated.add(id);
                }
                var progress = new VortexProgress(entry.getUUID("Owner"), members);
                progress.restore(VortexProgress.Status.valueOf(entry.getString("Status")), defeated,
                        entry.getInt("Elapsed"), entry.getInt("Absent"));
                data.encounters.put(entry.getUUID("Id"), progress);
            } catch (IllegalArgumentException ignored) {
                // Invalid encounters never authorize rewards; orphaned mobs are removed by the guard.
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var entries = new ListTag();
        encounters.forEach((id, progress) -> {
            var entry = new CompoundTag();
            entry.putUUID("Id", id);
            entry.putUUID("Owner", progress.owner());
            entry.putString("Status", progress.status().name());
            entry.putInt("Elapsed", progress.elapsed());
            entry.putInt("Absent", progress.absent());
            var members = new ListTag();
            for (UUID member : progress.members()) {
                var mob = new CompoundTag();
                mob.putUUID("Id", member);
                mob.putBoolean("Defeated", progress.defeated().contains(member));
                members.add(mob);
            }
            entry.put("Members", members);
            entries.add(entry);
        });
        tag.put("Encounters", entries);
        return tag;
    }
}
