package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.feature.fieldtools.FieldTools;
import com.jvprz.spectrobesreforged.common.feature.scanner.ScanTargets;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import java.util.List;

public final class ExtractionFieldToolItem extends Item {
    private final boolean blast;
    public ExtractionFieldToolItem(boolean blast){super(new Properties().durability(blast?128:256));this.blast=blast;}
    @Override public int getUseDuration(ItemStack stack,net.minecraft.world.entity.LivingEntity entity){return 2;}
    @Override public InteractionResult useOn(UseOnContext context){
        if(context.getPlayer()==null)return InteractionResult.PASS;
        context.getPlayer().startUsingItem(context.getHand());return InteractionResult.CONSUME;
    }
    @Override public ItemStack finishUsingItem(ItemStack stack,net.minecraft.world.level.Level level,net.minecraft.world.entity.LivingEntity entity){
        if(entity instanceof ServerPlayer player){
            var hit=getPlayerPOVHitResult(level,player,net.minecraft.world.level.ClipContext.Fluid.NONE);
            if(hit.getType()==net.minecraft.world.phys.HitResult.Type.BLOCK)activate(new UseOnContext(player,player.getUsedItemHand(),hit));
        }
        return stack;
    }
    /** Runs after the item-use transaction, so cancelled placement snapshots cannot duplicate rewards. */
    public InteractionResult activate(UseOnContext context){
        if(!(context.getPlayer() instanceof ServerPlayer player))return InteractionResult.SUCCESS;
        var level=player.serverLevel();var pos=context.getClickedPos();var stack=context.getItemInHand();
        if(player.getCooldowns().isOnCooldown(this)||!FieldTools.allowed(player,pos,stack))return InteractionResult.FAIL;
        if(blast){
            var place=pos.relative(context.getClickedFace());
            if(!FieldTools.canBreak(player,place,stack)||!level.getBlockState(place).canBeReplaced())return InteractionResult.FAIL;
            var charge=new PrimedTnt(level,place.getX()+.5,place.getY(),place.getZ()+.5,player);charge.setFuse(60);
            charge.getPersistentData().putBoolean("SpectrobesFieldBlast",true);charge.getPersistentData().putUUID("FieldBlastOwner",player.getUUID());
            if(!level.addFreshEntity(charge))return InteractionResult.FAIL;
            stack.hurtAndBreak(player.isCreative()?0:8,player,net.minecraft.world.entity.LivingEntity.getSlotForHand(context.getHand()));
            player.getCooldowns().addCooldown(this,60);level.playSound(null,place,SoundEvents.TNT_PRIMED,SoundSource.PLAYERS,.6f,1.1f);
            return InteractionResult.CONSUME;
        }
        if(ScanTargets.kind(level.getBlockState(pos))==null||!FieldTools.canBreak(player,pos,stack))return InteractionResult.FAIL;
        var raw=FieldTools.specimen(player,pos);String key=FieldTools.resultKey(raw);
        String result=key+(raw.getItem() instanceof RawFossilItem?"_fossil":"");
        if(key.isEmpty()||!player.getData(ModAttachments.EXCAVATION.get()).knows(result)){
            player.displayClientMessage(Component.translatable("message.spectrobesreforged.field_giga_unknown"),true);return InteractionResult.FAIL;
        }
        var clean=FieldTools.clean(raw,85);if(clean.isEmpty()||!level.destroyBlock(pos,false,player))return InteractionResult.FAIL;
        FieldTools.drop(player,pos,clean);
        stack.hurtAndBreak(player.isCreative()?0:8,player,net.minecraft.world.entity.LivingEntity.getSlotForHand(context.getHand()));
        player.getCooldowns().addCooldown(this,20);level.playSound(null,pos,SoundEvents.AMETHYST_BLOCK_CHIME,SoundSource.PLAYERS,.4f,1.2f);
        return InteractionResult.CONSUME;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){FieldTools.describe(stack,lines);}
}
