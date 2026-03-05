// src/main/java/com/jvprz/spectrobesreforged/common/event/SpectrobeCombatEvents.java
package com.jvprz.spectrobesreforged.common.event;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.network.ModSnapshotSender;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.UUID;

@EventBusSubscriber(modid = SpectrobesReforged.MODID)
public final class SpectrobeCombatEvents {
    private SpectrobeCombatEvents() {}

    @SubscribeEvent
    public static void onSpectrobeDamagePost(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof SpectrobeEntity spectrobe)) return;
        if (spectrobe.level().isClientSide) return;

        // Solo spectrobes “del Prizmod”
        if (!spectrobe.getPersistentData().hasUUID("SpectrobeId")) return;

        UUID sid = spectrobe.getPersistentData().getUUID("SpectrobeId");

        var owner = spectrobe.getOwner().orElse(null);
        if (!(owner instanceof ServerPlayer sp)) return;

        float amount = event.getNewDamage(); // daño FINAL ya aplicado (post)
        if (amount <= 0) return;

        int dmg = Math.max(1, (int) Math.ceil(amount));

        PrizmodData data = sp.getData(ModAttachments.PRIZMOD.get());

        // Baja HP en tus entries
        boolean ok = data.applyDamage(sid, dmg);
        if (!ok) return;

        ModSnapshotSender.sendSnapshot(sp, data);
    }
}