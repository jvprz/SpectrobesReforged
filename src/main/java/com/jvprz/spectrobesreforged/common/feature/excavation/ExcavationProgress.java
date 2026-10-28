package com.jvprz.spectrobesreforged.common.feature.excavation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;

/** Player skill and manual discovery catalogue; copied on death through its attachment. */
public final class ExcavationProgress {
    private int xp;
    private int completed;
    private final Set<String> catalogue = new HashSet<>();
    private final Set<String> crownedSpectrobes = new HashSet<>();
    public static final Codec<ExcavationProgress> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.optionalFieldOf("xp", 0).forGetter(ExcavationProgress::xp),
            Codec.INT.optionalFieldOf("completed",0).forGetter(ExcavationProgress::completed),
            Codec.STRING.listOf().optionalFieldOf("catalogue", List.of()).forGetter(p -> p.catalogue.stream().sorted().toList()),
            Codec.STRING.listOf().optionalFieldOf("crownedSpectrobes", List.of()).forGetter(p -> p.crownedSpectrobes.stream().sorted().toList())
    ).apply(inst, (xp, completed, entries, crowns) -> { var p = new ExcavationProgress(); p.xp = Math.clamp(xp, 0, 1000000); p.completed=Math.max(0,completed); p.catalogue.addAll(entries); p.crownedSpectrobes.addAll(crowns); return p; }));
    public void rememberCrown(UUID id) { crownedSpectrobes.add(id.toString()); }
    public boolean hasCrown(UUID id) { return crownedSpectrobes.contains(id.toString()); }
    public int completed(){return completed;}
    public int xp() { return xp; }
    public int level() { return Math.min(10, 1 + xp / 250); }
    public int progress() { return level() == 10 ? 250 : xp % 250; }
    public boolean knows(String result) { return catalogue.contains(result); }
    public void complete(String result, int earned) {
        if (earned <= 0) return;
        completed=Math.min(1000000,completed+1);
        xp = Math.min(1000000, xp + earned); catalogue.add(result);
    }
}
