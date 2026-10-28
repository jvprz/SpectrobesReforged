package com.jvprz.spectrobesreforged.common.feature.prizmod;

import com.jvprz.spectrobesreforged.common.content.item.PrizmodItem;
import com.jvprz.spectrobesreforged.common.feature.prizmod.menu.PrizmodMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public final class PrizmodAccess {
    private PrizmodAccess() {}
    public static boolean hasEquipped(Player player) {
        return com.jvprz.spectrobesreforged.common.compat.OptionalEquipment.hasPrizmod(player);
    }

    public static boolean canUse(Player player) {
        if (!player.isAlive() || player.isSpectator()) return false;
        if(hasEquipped(player))return true;
        for(int slot=0;slot<player.getInventory().getContainerSize();slot++)
            if(player.getInventory().getItem(slot).getItem() instanceof PrizmodItem)return true;
        return false;
    }
    public static void open(ServerPlayer player) {
        if(player.containerMenu!=player.inventoryMenu)return;
        if(!canUse(player)) {
            player.displayClientMessage(Component.translatable("message.spectrobesreforged.prizmod.required"),true);return;
        }
        PrizmodSync.sync(player);
        player.openMenu(new PrizmodMenuProvider());
    }
}
