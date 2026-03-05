// src/main/java/com/jvprz/spectrobesreforged/common/content/item/ChromaMineralItem.java
package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.UUID;

public class ChromaMineralItem extends Item {

    private final int variant; // 0/1/2

    public ChromaMineralItem(Properties props, int variant) {
        super(props);
        this.variant = variant;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {

        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS; // animación client-side
        }

        if (!(player instanceof ServerPlayer sp) || !(player.level() instanceof ServerLevel sl)) {
            return InteractionResult.PASS;
        }

        if (!(target instanceof SpectrobeEntity spectrobe)) {
            return InteractionResult.PASS;
        }

        var owner = spectrobe.getOwner().orElse(null);
        if (owner == null || !owner.getUUID().equals(sp.getUUID())) {
            sp.displayClientMessage(
                    Component.translatable("message.spectrobesreforged.not_your_spectrobe")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.CONSUME;
        }

        int current = spectrobe.getTextureVariant();
        if (current == variant) {
            sp.displayClientMessage(
                    Component.translatable("message.spectrobesreforged.already_color_variant", variant)
                            .withStyle(ChatFormatting.YELLOW),
                    true
            );
            return InteractionResult.CONSUME;
        }


        spectrobe.setTextureVariant(variant);

        sl.playSound(
                null,
                spectrobe.blockPosition(),
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS,
                1.0F,
                1.2F
        );

        sl.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                spectrobe.getX(), spectrobe.getY() + 0.6, spectrobe.getZ(),
                18,
                0.35, 0.45, 0.35,
                0.02
        );

        if (spectrobe.getPersistentData().hasUUID("SpectrobeId")) {
            UUID spectrobeId = spectrobe.getPersistentData().getUUID("SpectrobeId");

            PrizmodData data = sp.getData(ModAttachments.PRIZMOD.get());
            data.updateColorVariant(spectrobeId, variant); // <- lo añadimos abajo
        }

        if (!sp.getAbilities().instabuild) {
            stack.shrink(1);
        }

        sp.displayClientMessage(
                Component.translatable("message.spectrobesreforged.set_color_variant", variant)
                        .withStyle(ChatFormatting.GREEN),
                true
        );

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Component.translatable("tooltip.spectrobesreforged.chroma_" + variant)
                .withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, flag);
    }
}