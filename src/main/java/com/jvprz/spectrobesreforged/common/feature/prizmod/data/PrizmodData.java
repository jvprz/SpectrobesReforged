package com.jvprz.spectrobesreforged.common.feature.prizmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;

public class PrizmodData {

    private final List<SpectrobeEntry> box = new ArrayList<>();
    private final List<SpectrobeEntry> team = new ArrayList<>(Collections.nCopies(6, null));
    private SpectrobeEntry babySlot;

    public record TeamSlot(int slot, SpectrobeEntry entry) {
        public static final Codec<TeamSlot> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("slot").forGetter(TeamSlot::slot),
                SpectrobeEntry.CODEC.fieldOf("entry").forGetter(TeamSlot::entry)
        ).apply(inst, TeamSlot::new));
    }

    public static final Codec<PrizmodData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SpectrobeEntry.CODEC.listOf()
                    .optionalFieldOf("box", List.of())
                    .forGetter(d -> d.box),

            TeamSlot.CODEC.listOf()
                    .optionalFieldOf("team", List.of())
                    .forGetter(d -> {
                        List<TeamSlot> out = new ArrayList<>();
                        for (int i = 0; i < 6; i++) {
                            SpectrobeEntry e = d.team.get(i);
                            if (e != null) out.add(new TeamSlot(i, e));
                        }
                        return out;
                    }),

            SpectrobeEntry.CODEC.optionalFieldOf("babySlot")
                    .forGetter(d -> Optional.ofNullable(d.babySlot))
    ).apply(inst, (boxList, teamSlots, babyOpt) -> {

        PrizmodData d = new PrizmodData();
        d.box.addAll(boxList);

        d.team.clear();
        d.team.addAll(Collections.nCopies(6, null));

        for (TeamSlot ts : teamSlots) {
            int i = ts.slot();
            if (i >= 0 && i < 6) {
                d.team.set(i, ts.entry());
            }
        }

        d.babySlot = babyOpt.orElse(null);
        return d;
    }));

    // ===== BOX =====

    public List<SpectrobeEntry> getBox() {
        return Collections.unmodifiableList(box);
    }

    public Optional<SpectrobeEntry> findInBox(UUID id) {
        return box.stream().filter(e -> e.id().equals(id)).findFirst();
    }

    public void addToBox(SpectrobeEntry entry) {
        box.add(entry);
    }

    public boolean removeFromBox(UUID id) {
        return box.removeIf(e -> e.id().equals(id));
    }

    public boolean hasInBox(UUID id) {
        return box.stream().anyMatch(e -> e.id().equals(id));
    }

    public SpectrobeEntry getBoxAt(int index) {
        if (index < 0 || index >= box.size()) return null;
        return box.get(index);
    }

    public SpectrobeEntry removeBoxAt(int index) {
        if (index < 0 || index >= box.size()) return null;
        return box.remove(index);
    }

    public void insertBoxAt(int index, SpectrobeEntry entry) {
        if (entry == null) return;
        if (index < 0 || index > box.size()) box.add(entry);
        else box.add(index, entry);
    }

    // ===== BABY =====

    public Optional<SpectrobeEntry> getBabySlot() {
        return Optional.ofNullable(babySlot);
    }

    public void setBabySlot(SpectrobeEntry entryOrNull) {
        this.babySlot = entryOrNull;
    }

    // ===== TEAM (6) =====

    public List<SpectrobeEntry> getTeamView() {
        return Collections.unmodifiableList(team);
    }

    public Optional<SpectrobeEntry> getTeamSlot(int i) {
        if (i < 0 || i >= 6) return Optional.empty();
        return Optional.ofNullable(team.get(i));
    }

    public void setTeamSlot(int i, SpectrobeEntry entryOrNull) {
        if (i < 0 || i >= 6) return;
        team.set(i, entryOrNull);
    }

    // ===== BUSQUEDA / REEMPLAZO =====

    public Optional<SpectrobeEntry> findAnywhere(UUID id) {
        if (id == null) return Optional.empty();

        for (SpectrobeEntry e : box) {
            if (e != null && id.equals(e.id())) return Optional.of(e);
        }

        for (SpectrobeEntry e : team) {
            if (e != null && id.equals(e.id())) return Optional.of(e);
        }

        if (babySlot != null && id.equals(babySlot.id())) {
            return Optional.of(babySlot);
        }

        return Optional.empty();
    }

    public boolean replaceEntry(UUID id, SpectrobeEntry newEntry) {
        if (id == null || newEntry == null) return false;

        for (int i = 0; i < box.size(); i++) {
            SpectrobeEntry e = box.get(i);
            if (e != null && id.equals(e.id())) {
                box.set(i, newEntry);
                return true;
            }
        }

        for (int i = 0; i < team.size(); i++) {
            SpectrobeEntry e = team.get(i);
            if (e != null && id.equals(e.id())) {
                team.set(i, newEntry);
                return true;
            }
        }

        if (babySlot != null && id.equals(babySlot.id())) {
            babySlot = newEntry;
            return true;
        }

        return false;
    }

    // ===== CAMBIOS COMUNES =====

    public boolean updateColorVariant(UUID spectrobeId, int color) {
        Optional<SpectrobeEntry> found = findAnywhere(spectrobeId);
        if (found.isEmpty()) return false;
        return replaceEntry(spectrobeId, found.get().withColor(color));
    }

    public boolean applyDamage(UUID spectrobeId, int dmg) {
        if (spectrobeId == null || dmg <= 0) return false;

        Optional<SpectrobeEntry> found = findAnywhere(spectrobeId);
        if (found.isEmpty()) return false;

        SpectrobeEntry e = found.get();
        int next = Math.max(0, e.hpCur() - dmg);
        return replaceEntry(spectrobeId, e.withHpCur(next));
    }

    public boolean healFull(UUID spectrobeId) {
        Optional<SpectrobeEntry> found = findAnywhere(spectrobeId);
        if (found.isEmpty()) return false;
        return replaceEntry(spectrobeId, found.get().healFull());
    }

    public boolean updateStage(UUID spectrobeId, String newStage) {
        Optional<SpectrobeEntry> found = findAnywhere(spectrobeId);
        if (found.isEmpty()) return false;
        return replaceEntry(spectrobeId, found.get().withStage(newStage));
    }

    public boolean updateStats(UUID spectrobeId, int hp, int hpCur, int atk, int def) {
        Optional<SpectrobeEntry> found = findAnywhere(spectrobeId);
        if (found.isEmpty()) return false;
        return replaceEntry(spectrobeId, found.get().withStats(hp, hpCur, atk, def));
    }

    public boolean addMineralsFed(UUID spectrobeId, int amount) {
        if (spectrobeId == null || amount <= 0) return false;

        Optional<SpectrobeEntry> found = findAnywhere(spectrobeId);
        if (found.isEmpty()) return false;

        SpectrobeEntry e = found.get();
        return replaceEntry(spectrobeId, e.withMineralsFed(e.mineralsFed() + amount));
    }

    public boolean updateMineralBonuses(UUID spectrobeId, int hpBonus, int atkBonus, int defBonus) {
        Optional<SpectrobeEntry> found = findAnywhere(spectrobeId);
        if (found.isEmpty()) return false;

        SpectrobeEntry e = found.get();
        return replaceEntry(spectrobeId, e.withMineralBonuses(hpBonus, atkBonus, defBonus));
    }

    public boolean applyMineral(UUID spectrobeId, int addHp, int addAtk, int addDef) {
        Optional<SpectrobeEntry> found = findAnywhere(spectrobeId);
        if (found.isEmpty()) return false;

        SpectrobeEntry e = found.get();
        return replaceEntry(spectrobeId, e.applyMineralBonuses(addHp, addAtk, addDef));
    }

    public void healEquippedFull() {
        for (int i = 0; i < team.size(); i++) {
            SpectrobeEntry e = team.get(i);
            if (e != null) team.set(i, e.healFull());
        }

        if (babySlot != null) {
            babySlot = babySlot.healFull();
        }
    }
}