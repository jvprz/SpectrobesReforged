package com.jvprz.spectrobesreforged.client.compat;

import com.jvprz.spectrobesreforged.common.feature.prizmod.PrizmodAccess;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.client.KeyRegistry;

/** Referenced only after the optional-mod check on the physical client. */
public final class CuriosKeys {
    private CuriosKeys() {}
    public static boolean routeToEquipment(Player player,KeyMapping prizmodKey){
        var equipmentKey=KeyRegistry.openCurios;
        boolean shared=equipmentKey.getKey().equals(prizmodKey.getKey());
        if(shared&&!PrizmodAccess.canUse(player))return true;
        if(shared)while(equipmentKey.consumeClick()){}
        return false;
    }
}
