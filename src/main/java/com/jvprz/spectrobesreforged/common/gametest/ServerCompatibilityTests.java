package com.jvprz.spectrobesreforged.common.gametest;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.config.Config;
import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlRegistry;
import com.jvprz.spectrobesreforged.common.feature.krawl.factory.KrawlFactory;
import com.jvprz.spectrobesreforged.common.feature.prizmod.PrizmodAccess;
import com.jvprz.spectrobesreforged.common.feature.prizmod.menu.PrizmodMenu;
import com.jvprz.spectrobesreforged.common.network.*;
import com.jvprz.spectrobesreforged.common.registry.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import java.util.UUID;

/** Development-only discovery by NeoForge GameTestHooks; not registered in production worlds. */
@GameTestHolder(SpectrobesReforged.MODID)
@PrefixGameTestTemplate(false)
public final class ServerCompatibilityTests {
    @GameTest(template="server_smoke",timeoutTicks=100)
    public static void definitionsAndEntitiesLoadWithoutAClient(GameTestHelper helper){
        var snapshot=S2CGameData.capture().snapshot();
        helper.assertTrue(snapshot.ranges().size()>=22,"Server must load all bundled Child definitions");
        helper.assertTrue(snapshot.krawl().size()>=3,"Server must load the three Krawl");
        for(var definition:KrawlRegistry.all()){
            var entity=KrawlFactory.create(helper.getLevel(),definition);
            helper.assertTrue(entity.getDefinition().key().equals(definition.key()),"Krawl type mismatch");
            helper.assertTrue(entity.getMaxHealth()>0,"Krawl attributes missing");
            entity.setHealth(5);entity.refreshDefinitionFromRegistry();
            helper.assertTrue(entity.getHealth()==5,"Data reload must not heal existing Krawl");
        }
        helper.assertTrue(Config.SPEC.isLoaded(),"World/server config must be loaded");
        helper.succeed();
    }
    @GameTest(template="server_smoke",timeoutTicks=100)
    public static void prizmodAccessAndRateLimitsArePerPlayer(GameTestHelper helper){
        var level=helper.getLevel();
        var first=FakePlayerFactory.get(level,new GameProfile(UUID.fromString("67737458-6455-4e00-970d-07cf41955001"),"PrizmodTestA"));
        var second=FakePlayerFactory.get(level,new GameProfile(UUID.fromString("67737458-6455-4e00-970d-07cf41955002"),"PrizmodTestB"));
        first.getInventory().clearContent();second.getInventory().clearContent();
        helper.assertFalse(PrizmodAccess.canUse(first),"No Prizmod means no access");
        first.getInventory().setItem(10,new ItemStack(ModItems.WHITE_PRIZMOD.get()));
        helper.assertTrue(PrizmodAccess.canUse(first),"Inventory shortcut must accept a carried Prizmod");
        helper.assertFalse(PrizmodAccess.canUse(second),"Access cannot leak to another player");
        var menu=new PrizmodMenu(1,first.getInventory());
        helper.assertTrue(menu.stillValid(first),"Owned menu should stay valid");
        first.getInventory().clearContent();
        helper.assertFalse(menu.stillValid(first),"Removing the Prizmod revokes menu access");
        String action="gametest_"+UUID.randomUUID();
        helper.assertTrue(PlayerRequestLimits.allow(first,action,20),"First request should pass");
        helper.assertFalse(PlayerRequestLimits.allow(first,action,20),"Repeated request should be limited");
        helper.assertTrue(PlayerRequestLimits.allow(second,action,20),"Second player has an independent limit");
        helper.succeed();
    }
    @GameTest(template="server_smoke",timeoutTicks=100)
    public static void vortexTicksAndSettlesOnDedicatedServer(GameTestHelper helper){
        helper.setBlock(2,0,2,Blocks.STONE);
        var pos=helper.absolutePos(new BlockPos(2,3,2));
        var vortex=ModEntities.KRAWL_VORTEX.get().create(helper.getLevel());
        helper.assertTrue(vortex!=null,"Vortex construction failed");
        vortex.moveTo(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,0,0);
        helper.getLevel().addFreshEntity(vortex);
        helper.runAfterDelay(20,()->{
            helper.assertFalse(vortex.isRemoved(),"Untriggered vortex should remain loaded");
            helper.assertTrue(vortex.getY()<pos.getY(),"Airborne vortex must descend");
            var tag=new net.minecraft.nbt.CompoundTag();vortex.saveWithoutId(tag);
            helper.assertTrue(tag.getString("KrawlKey").matches("blova|viblova|blovant"),"Origin type must be saved");
            vortex.discard();helper.succeed();
        });
    }
}
