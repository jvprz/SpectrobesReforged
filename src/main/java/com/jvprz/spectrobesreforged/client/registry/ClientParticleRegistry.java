package com.jvprz.spectrobesreforged.client.registry;

import com.jvprz.spectrobesreforged.client.particle.DigitalNoteParticle;
import com.jvprz.spectrobesreforged.client.particle.SpectrobeTransferParticle;
import com.jvprz.spectrobesreforged.common.registry.ModParticles;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class ClientParticleRegistry {

    private ClientParticleRegistry() {
    }

    public static void registerParticleProviders(
            RegisterParticleProvidersEvent event
    ) {
        event.registerSpriteSet(
                ModParticles.DIGITAL_NOTE.get(),
                DigitalNoteParticle.Provider::new
        );

        event.registerSpriteSet(
                ModParticles.SPECTROBE_TRANSFER.get(),
                SpectrobeTransferParticle.Provider::new
        );
    }
}