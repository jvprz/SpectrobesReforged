package com.jvprz.spectrobesreforged.content.prizmod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;

public class PrizmodData {

    private final List<SpectrobeEntry> box = new ArrayList<>();
    private final List<SpectrobeEntry> team = new ArrayList<>(Collections.nCopies(6, null));
    private SpectrobeEntry babySlot;

    public static final Codec<PrizmodData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SpectrobeEntry.CODEC.listOf().optionalFieldOf("box", List.of()).forGetter(d -> d.box),
            SpectrobeEntry.CODEC.listOf().optionalFieldOf("team", List.of()).forGetter(d -> d.team),
            SpectrobeEntry.CODEC.optionalFieldOf("babySlot").forGetter(d -> Optional.ofNullable(d.babySlot))
    ).apply(inst, (boxList, teamList, babyOpt) -> {
        PrizmodData d = new PrizmodData();
        d.box.addAll(boxList);

        // Reconstruir team a 6 slots, rellenando nulls si faltan
        d.team.clear();
        d.team.addAll(Collections.nCopies(6, null));
        for (int i = 0; i < Math.min(6, teamList.size()); i++) {
            d.team.set(i, teamList.get(i));
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

    // Nuevos helpers por índice (para drag/drop)
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