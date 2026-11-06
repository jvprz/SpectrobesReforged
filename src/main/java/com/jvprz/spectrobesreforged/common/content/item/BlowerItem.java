package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.network.PlayerRequestLimits;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Hand-held air tool; separate from the excavation puzzle's virtual blower. */
public final class BlowerItem extends Item {
    public static final int DURABILITY = 256, INTERVAL = 5;
    public static final double RANGE = 5;
    private final boolean enhanced;
    public BlowerItem(){this(false);}
    public BlowerItem(boolean enhanced){super(new Properties().durability(enhanced?512:DURABILITY));this.enhanced=enhanced;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){com.jvprz.spectrobesreforged.common.feature.fieldtools.FieldTools.describe(stack,lines);}

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack=player.getItemInHand(hand);
        if (!player.isAlive() || player.isSpectator()) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        if (player instanceof ServerPlayer serverPlayer) pulse(serverPlayer,stack,hand);
        return InteractionResultHolder.consume(stack);
    }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 72000; }
    @Override public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        int elapsed=getUseDuration(stack,entity)-remaining;
        if (elapsed>0 && elapsed%INTERVAL==0 && entity instanceof ServerPlayer player)
            pulse(player,stack,player.getUsedItemHand());
    }
    private void pulse(ServerPlayer player, ItemStack stack, InteractionHand hand) {
        if (stack.isEmpty() || !player.isAlive() || player.isSpectator()
                || !PlayerRequestLimits.allow(player,"hand_blower",INTERVAL)) return;
        ServerLevel level=player.serverLevel();
        Vec3 origin=player.getEyePosition(), direction=player.getLookAngle().normalize();
        double range=enhanced?7:RANGE;
        for (var mob:level.getEntitiesOfClass(Mob.class,player.getBoundingBox().inflate(range+1),e->e.isAlive()&&!e.isRemoved())) {
            Vec3 target=mob.getEyePosition(),offset=target.subtract(origin);
            double distance=offset.length();
            if (distance>range || distance<.001 || offset.scale(1/distance).dot(direction)<(enhanced?.55:.72)) continue;
            if (level.clip(new ClipContext(origin,target,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player)).getType()!=HitResult.Type.MISS) continue;
            double force=1-Math.clamp(mob.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE),0,1);
            if (force<=0) continue;
            Vec3 push=direction.scale((enhanced?.8:.55)*force);
            mob.push(push.x,Math.max(.08*force,push.y*.4),push.z);
            mob.hurtMarked=true;
        }
        var obstacle=level.clip(new ClipContext(origin,origin.add(direction.scale(range)),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player));
        double visibleRange=obstacle.getType()==HitResult.Type.MISS ? range : obstacle.getLocation().distanceTo(origin);
        for (double distance:new double[]{1,2.5,4}) {
            if (distance>visibleRange) continue;
            Vec3 air=origin.add(direction.scale(distance));
            level.sendParticles(ParticleTypes.SMALL_GUST,air.x,air.y,air.z,1,.12,.12,.12,0);
        }
        if(enhanced){
            var centre=player.blockPosition().offset((int)Math.round(direction.x*3),0,(int)Math.round(direction.z*3));
            for(var pos:net.minecraft.core.BlockPos.betweenClosed(centre.offset(-1,0,-1),centre.offset(1,1,1))){
                var sight=level.clip(new ClipContext(origin,Vec3.atCenterOf(pos),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player));
                if(level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.SNOW)
                        &&(sight.getType()==HitResult.Type.MISS||sight.getBlockPos().equals(pos)))
                    com.jvprz.spectrobesreforged.common.feature.fieldtools.FieldTools.replace(player,pos,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),stack);
            }
        }
        if (player.tickCount%10<INTERVAL)
            level.playSound(null,player.blockPosition(),SoundEvents.BREEZE_SHOOT,SoundSource.PLAYERS,.35F,1.35F);
        if (!player.isCreative()) stack.hurtAndBreak(enhanced?2:1,player,LivingEntity.getSlotForHand(hand));
        if (stack.isEmpty()) player.stopUsingItem();
    }
}
