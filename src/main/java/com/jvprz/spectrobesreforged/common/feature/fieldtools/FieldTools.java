package com.jvprz.spectrobesreforged.common.feature.fieldtools;

import com.jvprz.spectrobesreforged.common.content.item.RawFossilItem;
import com.jvprz.spectrobesreforged.common.feature.excavation.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.*;
import java.util.*;

/** Server-side transactions shared by portable excavation equipment. */
public final class FieldTools {
    private FieldTools(){}
    public static void describe(ItemStack stack,List<Component> lines){
        lines.add(Component.translatable("tooltip.spectrobesreforged.field."+BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath()));
    }
    public static boolean allowed(ServerPlayer player,BlockPos pos,ItemStack tool){
        var level=player.serverLevel();
        return player.isAlive()&&!player.isSpectator()&&player.mayBuild()&&level.hasChunkAt(pos)
                &&level.mayInteract(player,pos)&&player.mayUseItemAt(pos,Direction.UP,tool);
    }
    public static boolean canBreak(ServerPlayer player,BlockPos pos,ItemStack tool){
        var level=player.serverLevel();var state=level.getBlockState(pos);
        return allowed(player,pos,tool)&&state.getDestroySpeed(level,pos)>=0&&!state.hasBlockEntity()
                &&!NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level,pos,state,player)).isCanceled();
    }
    public static boolean replace(ServerPlayer player,BlockPos pos,BlockState replacement,ItemStack tool){
        if(!canBreak(player,pos,tool))return false;
        var level=player.serverLevel();var old=level.getBlockState(pos);
        if(old.equals(replacement))return false;
        var snapshot=BlockSnapshot.create(level.dimension(),level,pos);
        if(!level.setBlock(pos,replacement,2))return false;
        if(EventHooks.onBlockPlace(player,snapshot,Direction.UP)){
            snapshot.restore(3);return false;
        }
        level.updateNeighborsAt(pos,replacement.getBlock());
        return true;
    }
    public static void wear(ServerPlayer player,ItemStack stack,int amount){
        if(!player.isCreative())stack.hurtAndBreak(amount,player,LivingEntity.getSlotForHand(player.getUsedItemHand()));
    }
    public static ItemStack specimen(ServerPlayer player,BlockPos pos){
        var level=player.serverLevel();var state=level.getBlockState(pos);
        var items=Block.getDrops(state,level,pos,null,player,Items.IRON_PICKAXE.getDefaultInstance());
        var entities=new ArrayList<ItemEntity>();
        for(var stack:items)entities.add(new ItemEntity(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,stack));
        var event=new BlockDropsEvent(level,pos,state,null,entities,player,Items.IRON_PICKAXE.getDefaultInstance());
        if(NeoForge.EVENT_BUS.post(event).isCanceled())return ItemStack.EMPTY;
        return event.getDrops().stream().map(ItemEntity::getItem).filter(ExcavationSpecimen::accepts).findFirst().orElse(ItemStack.EMPTY);
    }
    public static String resultKey(ItemStack raw){
        var tag=RawFossilItem.data(raw);String fixed=tag.getString(raw.getItem() instanceof RawFossilItem?"Fossil":"Mineral");
        return ExcavationSpecimen.candidates(raw).contains(fixed)?fixed:"";
    }
    public static ItemStack clean(ItemStack raw,int integrity){
        String key=resultKey(raw);if(key.isEmpty())return ItemStack.EMPTY;
        String id=key+(raw.getItem() instanceof RawFossilItem?"_fossil":"");
        var item=BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("spectrobesreforged",id));
        if(item==Items.AIR)return ItemStack.EMPTY;
        var result=new ItemStack(item);
        CustomData.update(DataComponents.CUSTOM_DATA,result,tag->{
            tag.putBoolean("ExcavationAutomatic",true);tag.putBoolean("ExcavationCrown",false);
            tag.putBoolean("ExcavationCooperative",false);tag.putInt("ExcavationIntegrity",Math.clamp(integrity,1,99));tag.putInt("ExcavationSeconds",0);
        });
        return result;
    }
    public static void drop(ServerPlayer player,BlockPos pos,ItemStack stack){
        if(stack.isEmpty())return;
        var entity=new ItemEntity(player.serverLevel(),pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,stack);
        entity.setTarget(player.getUUID());player.serverLevel().addFreshEntity(entity);
    }
}
