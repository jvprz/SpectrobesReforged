package com.jvprz.spectrobesreforged.common.compat;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

/** No optional-library types in this facade or the base items. */
public final class OptionalEquipment {
    private OptionalEquipment() {}
    public static boolean hasCurios(){return ModList.get().isLoaded("curios");}
    public static void register(IEventBus bus){
        if(hasCurios())bus.addListener(com.jvprz.spectrobesreforged.common.compat.curios.CuriosIntegration::setup);
    }
    public static boolean hasPrizmod(Player player){
        return hasCurios()&&com.jvprz.spectrobesreforged.common.compat.curios.CuriosIntegration.hasEquipped(player);
    }
}
