package com.jvprz.spectrobesreforged.content.prizmod;

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
            // BOX: lista completa
            SpectrobeEntry.CODEC.listOf()
                    .optionalFieldOf("box", List.of())
                    .forGetter(d -> d.box),

            // TEAM: guardamos solo ocupados PERO con índice
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

            // BABY
            SpectrobeEntry.CODEC.optionalFieldOf("babySlot")
                    .forGetter(d -> Optional.ofNullable(d.babySlot))
    ).apply(inst, (boxList, teamSlots, babyOpt) -> {

        PrizmodData d = new PrizmodData();
        d.box.addAll(boxList);

        // reconstruimos team a 6 slots (nulls)
        d.team.clear();
        d.team.addAll(Collections.nCopies(6, null));

        // reinsertamos en su slot real
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

    // helpers por índice (drag/drop)
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
}