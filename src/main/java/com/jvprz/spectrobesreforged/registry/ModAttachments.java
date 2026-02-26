package com.jvprz.spectrobesreforged.registry;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.content.prizmod.PrizmodData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public final class ModAttachments {
    private ModAttachments() {}

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SpectrobesReforged.MODID);

    public static final Supplier<AttachmentType<PrizmodData>> PRIZMOD =
            ATTACHMENT_TYPES.register("prizmod",
                    () -> AttachmentType.builder(PrizmodData::new)
                            .serialize(PrizmodData.CODEC)
                            .copyOnDeath() // recomendado para jugadores
                            .build()
            );

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}