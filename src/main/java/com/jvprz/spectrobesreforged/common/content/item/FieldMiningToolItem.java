package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.feature.fieldtools.FieldTools;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;

public final class FieldMiningToolItem extends PickaxeItem {
    private final boolean area;
    private static final Set<UUID> MINING=new HashSet<>();
    public FieldMiningToolItem(boolean area){
        super(Tiers.DIAMOND,new Properties().durability(area?1536:1024).attributes(PickaxeItem.createAttributes(Tiers.DIAMOND,1,-2.8f)));
        this.area=area;
    }
    @Override public float getDestroySpeed(ItemStack stack,BlockState state){
        float speed=super.getDestroySpeed(stack,state);return speed>1&&!area?speed*1.4f:speed;
    }
    @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,BlockPos pos,LivingEntity entity){
        boolean result=super.mineBlock(stack,level,state,pos,entity);
        if(!area||!(entity instanceof ServerPlayer player)||player.isShiftKeyDown()||!MINING.add(player.getUUID()))return result;
        try{
            var axis=Direction.getNearest(player.getLookAngle().x,player.getLookAngle().y,player.getLookAngle().z).getAxis();
            float hardness=state.getDestroySpeed(level,pos);
            for(int a=-1;a<=1;a++)for(int b=-1;b<=1;b++){
                if(a==0&&b==0||stack.isEmpty())continue;
                var target=axis==Direction.Axis.Y?pos.offset(a,0,b):axis==Direction.Axis.X?pos.offset(0,a,b):pos.offset(a,b,0);
                var other=level.getBlockState(target);float strength=other.getDestroySpeed(level,target);
                if(other.isAir()||other.hasBlockEntity()||strength<0||strength>Math.max(1,hardness)*1.5f
                        ||!stack.isCorrectToolForDrops(other)||!FieldTools.allowed(player,target,stack))continue;
                // Vanilla harvesting posts protection events, drops once and wears this tool once per block.
                player.gameMode.destroyBlock(target);
            }
        }finally{MINING.remove(player.getUUID());}
        return result;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){FieldTools.describe(stack,lines);}
}
