// src/main/java/com/jvprz/spectrobesreforged/common/event/SpectrobeDamageEvents.java
package com.jvprz.spectrobesreforged.common.event;

import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.network.ModSnapshotSender;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.UUID;

public final class SpectrobeDamageEvents {

    @SubscribeEvent
    public static void onLivingDamage(LivingIncomingDamageEvent event) {

        if (!(event.getEntity() instanceof SpectrobeEntity spectrobe)) return;
        if (spectrobe.level().isClientSide) return;

        var tag = spectrobe.getPersistentData();
        if (!tag.getBoolean("PrizmodBaby")) return;
        if (!tag.hasUUID("PrizmodOwner")) return;
        if (!tag.hasUUID("SpectrobeId")) return;

        float raw = event.getAmount();
        int dmg = Math.max(1, (int)Math.ceil(raw));

        UUID ownerId = tag.getUUID("PrizmodOwner");
        UUID spectrobeId = tag.getUUID("SpectrobeId");

        var owner = spectrobe.level().getPlayerByUUID(ownerId);
        if (!(owner instanceof ServerPlayer sp)) return;

        PrizmodData data = sp.getData(ModAttachments.PRIZMOD.get());

        var entryOpt = data.findAnywhere(spectrobeId);
        if (entryOpt.isEmpty()) return;

        SpectrobeEntry entry = entryOpt.get();

        int hpCurNew = Math.max(0, entry.hpCur() - dmg);

        data.replaceEntry(entry.id(), new SpectrobeEntry(
                entry.id(),
                entry.species(),
                entry.color(),
                entry.stage(),
                entry.level(),
                entry.hp(),
                hpCurNew,
                entry.atk(),
                entry.def()
        ));

        spectrobe.setHealth(Math.max(0f, hpCurNew));

        if (hpCurNew <= 0) {
            spectrobe.discard();
        }

        ModSnapshotSender.sendSnapshot(sp, data);
    }

    private static SpectrobeEntry updated(SpectrobeEntry e, int hpCurNew) {
        return new SpectrobeEntry(
                e.id(),
                e.species(),
                e.color(),
                e.stage(),
                e.level(),
                e.hp(),
                hpCurNew,
                e.atk(),
                e.def()
        );
    }
}