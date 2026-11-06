package com.jvprz.spectrobesreforged.common.feature.fieldtools;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.feature.scanner.ScanTargets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

@EventBusSubscriber(modid=SpectrobesReforged.MODID)
public final class FieldBlastEvents {
    private FieldBlastEvents(){}
    @SubscribeEvent public static void detonate(ExplosionEvent.Detonate event){
        var source=event.getExplosion().getDirectSourceEntity();
        if(source==null||!source.getPersistentData().getBoolean("SpectrobesFieldBlast")||!(event.getLevel() instanceof ServerLevel level))return;
        var owner=source.getPersistentData();
        ServerPlayer player=source instanceof net.minecraft.world.entity.item.PrimedTnt tnt&&tnt.getOwner() instanceof ServerPlayer online?online:
                owner.hasUUID("FieldBlastOwner")?level.getServer().getPlayerList().getPlayer(owner.getUUID("FieldBlastOwner")):null;
        if(player==null||player.isRemoved()||player.level()!=level){event.getAffectedBlocks().clear();event.getAffectedEntities().clear();return;}
        var tool=Items.IRON_PICKAXE.getDefaultInstance();
        var iterator=event.getAffectedBlocks().iterator();
        while(iterator.hasNext()){
            var pos=iterator.next();
            if(pos.getCenter().distanceToSqr(source.position())>6.25||!FieldTools.canBreak(player,pos,tool)){iterator.remove();continue;}
            if(ScanTargets.kind(level.getBlockState(pos))==null)continue;
            iterator.remove(); // This deposit is resolved once here, never by vanilla explosion loot.
            var raw=FieldTools.specimen(player,pos);
            if(!level.destroyBlock(pos,false,player))continue;
            if(level.random.nextBoolean())FieldTools.drop(player,pos,FieldTools.clean(raw,40+level.random.nextInt(41)));
        }
    }
}
