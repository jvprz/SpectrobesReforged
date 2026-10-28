package com.jvprz.spectrobesreforged.common.feature.training;

import com.jvprz.spectrobesreforged.common.feature.prizmod.PrizmodSync;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.feature.prizmod.logic.SpectrobeManager;
import com.jvprz.spectrobesreforged.common.feature.progression.ModProgression;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class SpectrobeTraining {
    private SpectrobeTraining(){}
    public static void syncActive(ServerPlayer player,SpectrobeEntry entry){
        var entity=SpectrobeManager.findActiveBaby(player.serverLevel(),player);
        if(entity==null||!entity.getPersistentData().hasUUID("SpectrobeId")||!entry.id().equals(entity.getPersistentData().getUUID("SpectrobeId")))return;
        var attribute=entity.getAttribute(Attributes.MAX_HEALTH);if(attribute!=null)attribute.setBaseValue(Math.max(1,entry.hp()));
        entity.setHealth(Math.max(0,Math.min(entry.hpCur(),entry.hp())));
        entity.setCustomName(SpectrobeNames.name(entry.species(),entry.crowned(),entry.cooperative()));
        entity.setCustomNameVisible(entry.crowned()||entry.cooperative());
    }
    public static void levelled(ServerPlayer player,SpectrobeEntry before,SpectrobeEntry after){
        for(int milestone:new int[]{10,25,50,100})if(after.level()>=milestone)ModProgression.award(player,"spectrobe_level_"+milestone);
        if(after.level()>before.level()){
            player.displayClientMessage(Component.translatable("message.spectrobesreforged.level_up",SpectrobeNames.name(after.species(),after.crowned(),after.cooperative()),after.level()),true);
            player.serverLevel().playSound(null,player.blockPosition(),SoundEvents.PLAYER_LEVELUP,SoundSource.PLAYERS,.45F,1.4F);
        }
    }
    public static void victory(ServerPlayer player){victory(player,60);}
    public static void victory(ServerPlayer player,int xp){
        var data=player.getData(ModAttachments.PRIZMOD.get());boolean changed=false;
        for(var entry:data.getTeamView())if(entry!=null&&!entry.isChild()&&!entry.isDead()){
            var updated=SpectrobeLevels.award(entry.withProgress(entry.progress().won()),Math.clamp(xp,0,60),SpectrobeSpeciesRegistry.getByKey(entry.species()));
            data.replaceEntry(entry.id(),updated);levelled(player,entry,updated);changed=true;
        }
        if(changed)PrizmodSync.sync(player,data);
    }
}
