package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, SpectrobesReforged.MODID);

    public static final Supplier<SoundEvent> SCANNER_OPEN =
            registerSound("scanner_open");

    public static final Supplier<SoundEvent> SCANNER_FOUND =
            registerSound("scanner_found");

    public static final Supplier<SoundEvent> KRAWL_IDLE =
            registerSound("entity.krawl.idle");

    public static final Supplier<SoundEvent> KRAWL_ATTACK =
            registerSound("entity.krawl.attack");

    public static final Supplier<SoundEvent> KRAWL_HURT =
            registerSound("entity.krawl.hurt");

    public static final Supplier<SoundEvent> KRAWL_DEATH =
            registerSound("entity.krawl.death");

    private static Supplier<SoundEvent> registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, name);

        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(net.neoforged.bus.api.IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }

    private ModSounds() {}
}