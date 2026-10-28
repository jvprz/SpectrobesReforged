package com.jvprz.spectrobesreforged.client.event;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.client.input.ModKeybinds;
import com.jvprz.spectrobesreforged.common.network.C2STriggerSpectrobeRangeScan;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
        modid = SpectrobesReforged.MODID,
        value = Dist.CLIENT
)
public final class ClientInputEvents {

    private ClientInputEvents() {}

    @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST)
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (ModKeybinds.RANGE_SCAN.consumeClick()) {
            if(mc.screen==null)PacketDistributor.sendToServer(new C2STriggerSpectrobeRangeScan());
        }
        while (ModKeybinds.OPEN_PRIZMOD.consumeClick()) {
            if(mc.screen!=null)continue;
            if(com.jvprz.spectrobesreforged.common.compat.OptionalEquipment.hasCurios()
                    &&com.jvprz.spectrobesreforged.client.compat.CuriosKeys.routeToEquipment(mc.player,ModKeybinds.OPEN_PRIZMOD))continue;
            PacketDistributor.sendToServer(new com.jvprz.spectrobesreforged.common.network.C2SOpenPrizmod());
        }
    }
}