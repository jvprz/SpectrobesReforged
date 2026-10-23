package com.jvprz.spectrobesreforged.client.registry;

import com.jvprz.spectrobesreforged.client.particle.DigitalNoteParticle;
import com.jvprz.spectrobesreforged.client.particle.VortexGasParticle;
import com.jvprz.spectrobesreforged.client.particle.SpectrobeTransferParticle;
import com.jvprz.spectrobesreforged.common.registry.ModParticles;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class ClientParticleRegistry {

    private ClientParticleRegistry() {
    }

    public static void registerParticleProviders(
            RegisterParticleProvidersEvent event
    ) {
        event.registerSpriteSet(ModParticles.VORTEX_GAS.get(), VortexGasParticle.Provider::new);
        event.registerSpriteSet(ModParticles.VORTEX_SHADOW_GAS.get(),
                sprites -> new VortexGasParticle.Provider(sprites, true));
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