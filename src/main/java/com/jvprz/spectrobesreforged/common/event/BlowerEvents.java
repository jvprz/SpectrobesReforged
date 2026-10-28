package com.jvprz.spectrobesreforged.common.event;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.item.BlowerItem;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Aiming the blower at a mob uses the tool rather than trading or toggling pet sitting. */
@EventBusSubscriber(modid=SpectrobesReforged.MODID)
public final class BlowerEvents {
    private BlowerEvents() {}
    @SubscribeEvent public static void useOnMob(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Mob) || !(event.getItemStack().getItem() instanceof BlowerItem blower)) return;
        var result=blower.use(event.getLevel(),event.getEntity(),event.getHand());
        event.setCanceled(true);event.setCancellationResult(result.getResult());
    }
    @SubscribeEvent public static void useOnMobPart(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!(event.getTarget() instanceof Mob) || !(event.getItemStack().getItem() instanceof BlowerItem blower)) return;
        var result=blower.use(event.getLevel(),event.getEntity(),event.getHand());
        event.setCanceled(true);event.setCancellationResult(result.getResult());
    }
}
