package com.jvprz.spectrobesreforged.common.feature.prizmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record SpectrobeEntry(UUID id, String species, boolean baby) {

    public static final Codec<SpectrobeEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(SpectrobeEntry::id),
            Codec.STRING.fieldOf("species").forGetter(SpectrobeEntry::species),
            Codec.BOOL.fieldOf("baby").forGetter(SpectrobeEntry::baby)
    ).apply(inst, SpectrobeEntry::new));
}