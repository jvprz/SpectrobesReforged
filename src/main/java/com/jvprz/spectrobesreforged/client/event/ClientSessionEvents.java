package com.jvprz.spectrobesreforged.client.event;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.network.SyncedGameData;
import com.jvprz.spectrobesreforged.client.feature.prizmod.ClientPrizmodState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import java.util.List;

@EventBusSubscriber(modid=SpectrobesReforged.MODID,value=Dist.CLIENT)
public final class ClientSessionEvents {
    private ClientSessionEvents() {}
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event){
        SyncedGameData.clear();ClientPrizmodState.setSnapshot(List.of(),List.of(),null);
    }
}
