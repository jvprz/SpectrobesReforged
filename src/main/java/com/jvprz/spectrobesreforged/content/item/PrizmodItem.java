package com.jvprz.spectrobesreforged.content.item;

import com.jvprz.spectrobesreforged.client.prizmod.ClientPrizmodState;
import com.jvprz.spectrobesreforged.client.screen.PrizmodScreen;
import com.jvprz.spectrobesreforged.content.prizmod.PrizmodData;
import com.jvprz.spectrobesreforged.network.S2CPrizmodSnapshot;
import com.jvprz.spectrobesreforged.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class PrizmodItem extends Item {

    public PrizmodItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {

            PrizmodData data = sp.getData(ModAttachments.PRIZMOD.get());

            var box = data.getBox().stream()
                    .map(e -> new S2CPrizmodSnapshot.Entry(e.id(), e.species(), e.baby()))
                    .toList();

            List<S2CPrizmodSnapshot.Entry> team =
                    java.util.Arrays.<S2CPrizmodSnapshot.Entry>asList(null, null, null, null, null, null);

            S2CPrizmodSnapshot.Entry baby = data.getBabySlot()
                    .map(e -> new S2CPrizmodSnapshot.Entry(e.id(), e.species(), e.baby()))
                    .orElse(null);

            PacketDistributor.sendToPlayer(sp,
                    new S2CPrizmodSnapshot(box, team, baby)
            );

            sp.openMenu(new PrizmodMenuProvider());
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}