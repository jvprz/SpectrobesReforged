package com.jvprz.spectrobesreforged.content.prizmod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.*;

public class PrizmodData {

    private final List<SpectrobeEntry> box = new ArrayList<>();
    private SpectrobeEntry babySlot;

    public static final Codec<PrizmodData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SpectrobeEntry.CODEC.listOf().optionalFieldOf("box", List.of()).forGetter(d -> d.box),
            SpectrobeEntry.CODEC.optionalFieldOf("babySlot").forGetter(d -> Optional.ofNullable(d.babySlot))
    ).apply(inst, (boxList, babyOpt) -> {
        PrizmodData d = new PrizmodData();
        d.box.addAll(boxList);
        d.babySlot = babyOpt.orElse(null);
        return d;
    }));

    public List<SpectrobeEntry> getBox() {
        return Collections.unmodifiableList(box);
    }

    public Optional<SpectrobeEntry> getBabySlot() {
        return Optional.ofNullable(babySlot);
    }

    public void setBabySlot(SpectrobeEntry entryOrNull) {
        this.babySlot = entryOrNull;
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
}