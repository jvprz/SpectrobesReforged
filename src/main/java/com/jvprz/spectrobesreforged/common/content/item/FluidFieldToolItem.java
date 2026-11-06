package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.feature.fieldtools.FieldTools;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.tags.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.HitResult;
import java.util.List;

public final class FluidFieldToolItem extends Item {
    public enum Kind { FIRE, WATER, VACUUM }
    private final Kind kind;
    public FluidFieldToolItem(Kind kind){super(new Properties().durability(kind==Kind.VACUUM?512:384));this.kind=kind;}
    public int capacity(){return kind==Kind.VACUUM?16:8;}
    public static int water(ItemStack stack){return Math.clamp(RawFossilItem.data(stack).getInt("FieldWater"),0,16);}
    private void water(ItemStack stack,int value){CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.putInt("FieldWater",Math.clamp(value,0,capacity())));}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 72000;}
    @Override public InteractionResult useOn(UseOnContext context){return context.getPlayer()==null?InteractionResult.PASS:use(context.getLevel(),context.getPlayer(),context.getHand()).getResult();}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);if(!player.isAlive()||player.isSpectator())return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    @Override public void onUseTick(Level level,LivingEntity entity,ItemStack stack,int remaining){
        int elapsed=getUseDuration(stack,entity)-remaining;
        if(elapsed%8==0&&entity instanceof ServerPlayer player)pulse(player,stack);
    }
    public boolean pulse(ServerPlayer player,ItemStack stack){
        if(stack.isEmpty()||!player.isAlive()||player.isSpectator()||!com.jvprz.spectrobesreforged.common.network.PlayerRequestLimits.allow(player,"field_fluid",8))return false;
        var level=player.serverLevel();
        var hit=getPlayerPOVHitResult(level,player,kind==Kind.FIRE?ClipContext.Fluid.NONE:kind==Kind.WATER?ClipContext.Fluid.ANY:ClipContext.Fluid.SOURCE_ONLY);
        if(hit.getType()!=HitResult.Type.BLOCK)return false;
        var pos=hit.getBlockPos();var state=level.getBlockState(pos);boolean changed=false;
        if(kind!=Kind.FIRE&&player.isShiftKeyDown()){
            var target=pos.relative(hit.getDirection());
            if(water(stack)>0&&!level.dimensionType().ultraWarm()&&level.getBlockState(target).isAir()){
                changed=FieldTools.replace(player,target,Blocks.WATER.defaultBlockState(),stack);if(changed)water(stack,water(stack)-1);
            }
        }else if(kind!=Kind.FIRE&&state.getFluidState().is(FluidTags.WATER)&&state.getFluidState().isSource()){
            if(water(stack)<capacity()){
                var replacement=state.hasProperty(BlockStateProperties.WATERLOGGED)?state.setValue(BlockStateProperties.WATERLOGGED,false):Blocks.AIR.defaultBlockState();
                changed=FieldTools.replace(player,pos,replacement,stack);if(changed)water(stack,water(stack)+1);
            }
        }else if(kind==Kind.FIRE){
            if(state.is(Blocks.SNOW)||state.is(Blocks.SNOW_BLOCK))changed=FieldTools.replace(player,pos,Blocks.AIR.defaultBlockState(),stack);
            else if(state.is(Blocks.ICE)||state.is(Blocks.PACKED_ICE)||state.is(Blocks.BLUE_ICE))
                changed=FieldTools.replace(player,pos,(level.dimensionType().ultraWarm()?Blocks.AIR:Blocks.WATER).defaultBlockState(),stack);
        }else if(kind==Kind.WATER&&water(stack)>0){
            if(state.is(Blocks.LAVA))changed=FieldTools.replace(player,pos,(state.getFluidState().isSource()?Blocks.OBSIDIAN:Blocks.COBBLESTONE).defaultBlockState(),stack);
            else if(state.is(Blocks.MAGMA_BLOCK))changed=FieldTools.replace(player,pos,Blocks.BASALT.defaultBlockState(),stack);
            else{
                if(!state.is(BlockTags.FIRE))pos=pos.relative(hit.getDirection());
                if(level.getBlockState(pos).is(BlockTags.FIRE))changed=FieldTools.replace(player,pos,Blocks.AIR.defaultBlockState(),stack);
            }
            if(changed)water(stack,water(stack)-1);
        }
        if(changed){
            FieldTools.wear(player,stack,kind==Kind.FIRE?2:1);
            level.playSound(null,pos,kind==Kind.FIRE?SoundEvents.FIRE_EXTINGUISH:SoundEvents.BUCKET_EMPTY,SoundSource.PLAYERS,.35f,1.2f);
            level.sendParticles(kind==Kind.FIRE?ParticleTypes.SMOKE:ParticleTypes.SPLASH,pos.getX()+.5,pos.getY()+.7,pos.getZ()+.5,8,.2,.2,.2,.03);
        }
        return changed;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        FieldTools.describe(stack,lines);if(kind!=Kind.FIRE)lines.add(Component.translatable("tooltip.spectrobesreforged.field_water",water(stack),capacity()));
    }
}
