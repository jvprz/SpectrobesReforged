package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraft.world.entity.Entity;

public final class ModSpectrobePortalGuard {
    private ModSpectrobePortalGuard() {}

    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
        Entity e = event.getEntity();

        // Si es tu bebé Prizmod, no puede cruzar dimensiones
        if (e instanceof SpectrobeEntity) {
            var tag = e.getPersistentData();
            if (tag.getBoolean("PrizmodBaby")) {
                event.setCanceled(true);
            }
        }
    }
}