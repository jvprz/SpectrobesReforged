package com.jvprz.spectrobesreforged.common.compat.curios;

import com.jvprz.spectrobesreforged.common.content.item.PrizmodItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/** Loaded only when Curios is installed. The plain Prizmod item remains library-independent. */
public final class CuriosIntegration {
    private CuriosIntegration() {}
    public static void setup(FMLCommonSetupEvent event){
        event.enqueueWork(()->{
            ICurioItem behavior=new ICurioItem(){
                @Override public boolean canEquip(SlotContext context,ItemStack stack){return context.identifier().equals("prizmod");}
                @Override public boolean canEquipFromUse(SlotContext context,ItemStack stack){return false;}
            };
            for(var item:BuiltInRegistries.ITEM)if(item instanceof PrizmodItem)CuriosApi.registerCurio(item,behavior);
        });
    }
    public static boolean hasEquipped(Player player){
        return CuriosApi.getCuriosInventory(player).flatMap(inventory->inventory.getStacksHandler("prizmod"))
                .map(handler->{
                    var stacks=handler.getStacks();
                    for(int slot=0;slot<stacks.getSlots();slot++)if(stacks.getStackInSlot(slot).getItem() instanceof PrizmodItem)return true;
                    return false;
                }).orElse(false);
    }
}
