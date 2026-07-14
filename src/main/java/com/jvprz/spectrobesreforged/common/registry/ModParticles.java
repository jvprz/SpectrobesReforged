package com.jvprz.spectrobesreforged.common.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(
                    BuiltInRegistries.PARTICLE_TYPE,
                    "spectrobesreforged"
            );

    public static final DeferredHolder<
            ParticleType<?>,
            SimpleParticleType
            > DIGITAL_NOTE =
            PARTICLE_TYPES.register(
                    "digital_note",
                    () -> new SimpleParticleType(false)
            );

    public static final DeferredHolder<
            ParticleType<?>,
            SimpleParticleType
            > SPECTROBE_TRANSFER =
            PARTICLE_TYPES.register(
                    "spectrobe_transfer",
                    () -> new SimpleParticleType(false)
            );

    private ModParticles() {
    }

    public static void register(
            IEventBus eventBus
    ) {
        PARTICLE_TYPES.register(eventBus);
    }
}