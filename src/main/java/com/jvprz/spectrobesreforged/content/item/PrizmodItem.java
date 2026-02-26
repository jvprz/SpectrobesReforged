package com.jvprz.spectrobesreforged.content.item;

import com.jvprz.spectrobesreforged.client.screen.PrizmodScreen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PrizmodItem extends Item {

    public PrizmodItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new PrizmodMenuProvider());
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}