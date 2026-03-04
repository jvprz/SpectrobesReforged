package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.menu.PrizmodMenuProvider;
import com.jvprz.spectrobesreforged.common.network.S2CPrizmodSnapshot;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.List;

public class PrizmodItem extends Item {

    public PrizmodItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide && player instanceof ServerPlayer sp) {

            PrizmodData data = sp.getData(ModAttachments.PRIZMOD.get());

            List<S2CPrizmodSnapshot.Entry> box = data.getBox().stream()
                    .map(e -> new S2CPrizmodSnapshot.Entry(
                            e.id(),
                            e.species(),
                            e.color(),
                            e.stage(),
                            e.level(),
                            e.hp(),
                            e.atk(),
                            e.def()
                    ))
                    .toList();

            // Team: por ahora vacío (6 slots)
            List<S2CPrizmodSnapshot.Entry> team =
                    Arrays.asList(null, null, null, null, null, null);

            S2CPrizmodSnapshot.Entry baby = data.getBabySlot()
                    .map(e -> new S2CPrizmodSnapshot.Entry(
                            e.id(),
                            e.species(),
                            e.color(),
                            e.stage(),
                            e.level(),
                            e.hp(),
                            e.atk(),
                            e.def()
                    ))
                    .orElse(null);

            PacketDistributor.sendToPlayer(sp, new S2CPrizmodSnapshot(box, team, baby));

            sp.openMenu(new PrizmodMenuProvider());
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}