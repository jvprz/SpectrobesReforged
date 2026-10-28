package com.jvprz.spectrobesreforged.common.event;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.network.S2CGameData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid=SpectrobesReforged.MODID)
public final class DefinitionSyncEvents {
    private DefinitionSyncEvents() {}
    @SubscribeEvent public static void sync(OnDatapackSyncEvent event){
        if(event.getPlayer()==null)for(var level:event.getPlayerList().getServer().getAllLevels())
            for(var entity:level.getAllEntities())if(entity instanceof com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlEntity krawl)
                krawl.refreshDefinitionFromRegistry();
        var packet=S2CGameData.capture();
        event.getRelevantPlayers().forEach(player->PacketDistributor.sendToPlayer(player,packet));
    }
}
