package com.jvprz.spectrobesreforged.client.event;

import com.jvprz.spectrobesreforged.client.feature.prizmod.ClientPrizmodState;
import com.jvprz.spectrobesreforged.common.network.S2CGameData;
import com.jvprz.spectrobesreforged.common.network.S2CPrizmodSnapshot;
import com.jvprz.spectrobesreforged.common.network.SyncedGameData;
import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlEntity;
import net.minecraft.client.Minecraft;

public final class ClientPayloadHandlers {
    private ClientPayloadHandlers() {}
    private static ClientPrizmodState.Entry client(S2CPrizmodSnapshot.Entry e){
        return e==null?null:new ClientPrizmodState.Entry(e.id(),e.species(),e.color(),e.stage(),e.level(),e.hp(),e.hpCur(),e.atk(),e.def(),
                e.mineralsFed(),e.mineralHpBonus(),e.mineralAtkBonus(),e.mineralDefBonus(),e.progress(),e.element());
    }
    public static void prizmod(S2CPrizmodSnapshot packet){
        ClientPrizmodState.setSnapshot(packet.box().stream().map(ClientPayloadHandlers::client).toList(),
                packet.team().stream().map(ClientPayloadHandlers::client).toList(),client(packet.baby()));
    }
    public static void definitions(S2CGameData packet){
        SyncedGameData.apply(packet.snapshot());
        var level=Minecraft.getInstance().level;
        if(level!=null)for(var entity:level.entitiesForRendering())if(entity instanceof KrawlEntity)entity.refreshDimensions();
    }
    public static void excavation(com.jvprz.spectrobesreforged.common.network.S2CExcavationCoop packet){
        var player=Minecraft.getInstance().player;
        if(player!=null&&player.containerMenu instanceof com.jvprz.spectrobesreforged.common.feature.excavation.ExcavationMenu menu&&menu.containerId==packet.container())menu.setCoop(packet.cursors());
    }
}
