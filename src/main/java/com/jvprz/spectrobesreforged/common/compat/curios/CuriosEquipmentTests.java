package com.jvprz.spectrobesreforged.common.compat.curios;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.prizmod.PrizmodAccess;
import com.jvprz.spectrobesreforged.common.feature.prizmod.menu.PrizmodMenu;
import com.jvprz.spectrobesreforged.common.registry.ModItems;
import com.mojang.authlib.GameProfile;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import java.util.UUID;

@PrefixGameTestTemplate(false)
public final class CuriosEquipmentTests {
    @GameTest(templateNamespace=SpectrobesReforged.MODID,template="server_smoke",timeoutTicks=100)
    public static void equippedPrizmodWorksWithAFullInventoryAndPersists(GameTestHelper helper){
        var player=FakePlayerFactory.get(helper.getLevel(),new GameProfile(
                UUID.fromString("67737458-6455-4e00-970d-07cf41955003"),"PrizmodEquipTest"));
        player.getInventory().clearContent();
        var inventory=top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player).orElseThrow();
        var equipped=inventory.getStacksHandler("prizmod").orElseThrow().getStacks();
        helper.assertTrue(equipped.getSlots()==1,"Exactly one dedicated Prizmod slot must exist");
        var context=new top.theillusivec4.curios.api.SlotContext("prizmod",player,0,false,true);
        helper.assertFalse(top.theillusivec4.curios.api.CuriosApi.isStackValid(context,new ItemStack(net.minecraft.world.item.Items.STONE)),"Other items must not fit");
        int colors=0;
        for(var item:net.minecraft.core.registries.BuiltInRegistries.ITEM)
            if(item instanceof com.jvprz.spectrobesreforged.common.content.item.PrizmodItem){
                helper.assertTrue(top.theillusivec4.curios.api.CuriosApi.isStackValid(context,new ItemStack(item)),"Every Prizmod color must fit");colors++;
            }
        helper.assertTrue(colors==16,"All sixteen Prizmod colors should be registered");
        player.getInventory().setItem(0,new ItemStack(ModItems.WHITE_PRIZMOD.get()));
        var remainder=equipped.insertItem(0,player.getInventory().getItem(0).copy(),false);
        player.getInventory().setItem(0,remainder);
        helper.assertTrue(remainder.isEmpty(),"Equipping should move the Prizmod out of normal inventory");
        for(int slot=0;slot<36;slot++)player.getInventory().setItem(slot,new ItemStack(net.minecraft.world.item.Items.STONE,64));
        helper.assertTrue(player.getInventory().getFreeSlot()==-1,"All 36 normal slots should remain usable");
        helper.assertTrue(PrizmodAccess.hasEquipped(player)&&PrizmodAccess.canUse(player),"G access must recognize equipped gear");
        var menu=new PrizmodMenu(1,player.getInventory());
        helper.assertTrue(menu.stillValid(player),"Collection menu must accept equipped gear");
        var saved=inventory.saveInventory(true);
        helper.assertFalse(PrizmodAccess.canUse(player),"Removing equipment must revoke access with only stone carried");
        inventory.loadInventory(saved);
        helper.assertTrue(PrizmodAccess.hasEquipped(player),"Equipment must restore from saved Curios data");
        helper.assertTrue(player.getInventory().getFreeSlot()==-1,"Restoring equipment must not consume an inventory slot");
        inventory.saveInventory(true);player.getInventory().clearContent();
        helper.succeed();
    }
}
