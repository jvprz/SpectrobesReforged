package com.jvprz.spectrobesreforged.common.event;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Claiming the mouth item takes precedence over using the item in the player's hand. */
@EventBusSubscriber(modid=SpectrobesReforged.MODID)
public final class CarriedMineralEvents {
    private CarriedMineralEvents() {}
    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void interact(PlayerInteractEvent.EntityInteract event) {
        if(!(event.getTarget() instanceof SpectrobeEntity mob) || !mob.hasCarriedMineral())return;
        if(event.getEntity() instanceof ServerPlayer owner)mob.claimCarriedMineral(owner);
        event.setCanceled(true);event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
    }
    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void interactPart(PlayerInteractEvent.EntityInteractSpecific event) {
        if(!(event.getTarget() instanceof SpectrobeEntity mob) || !mob.hasCarriedMineral())return;
        if(event.getEntity() instanceof ServerPlayer owner)mob.claimCarriedMineral(owner);
        event.setCanceled(true);event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
    }
}
