package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.feature.fieldtools.FieldTools;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import java.util.List;

public final class RecoveryFieldToolItem extends Item {
    public RecoveryFieldToolItem(){super(new Properties().durability(24));}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 32;}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.DRINK;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(player.getCooldowns().isOnCooldown(this)||!player.isAlive()||player.isSpectator()||player.getHealth()>=player.getMaxHealth())return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);return InteractionResultHolder.consume(stack);
    }
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity entity){
        if(entity instanceof ServerPlayer player&&!player.getCooldowns().isOnCooldown(this)&&player.isAlive()&&player.getHealth()<player.getMaxHealth()){
            player.heal(4);FieldTools.wear(player,stack,1);player.getCooldowns().addCooldown(this,600);
        }
        return stack;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){FieldTools.describe(stack,lines);}
}
