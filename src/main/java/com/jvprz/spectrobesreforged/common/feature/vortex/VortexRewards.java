package com.jvprz.spectrobesreforged.common.feature.vortex;

import com.jvprz.spectrobesreforged.common.registry.ModItems;
import com.jvprz.spectrobesreforged.common.registry.ModSounds;
import com.jvprz.spectrobesreforged.common.feature.progression.ModProgression;
import com.jvprz.spectrobesreforged.common.feature.training.SpectrobeTraining;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

public final class VortexRewards {
    private VortexRewards() {}
    public record Budget(int c,int b,int xp,int trainingXp) {}
    public static Budget budget(int players,int deaths){
        int size=Math.clamp(players,1,VortexProgress.MAX_PARTICIPANTS),d=Math.clamp(deaths,0,3);
        int c=size>=2?3:2,b=size>=3?1:0,xp=Math.min(30,10+size*5);
        if(d>0){c=Math.max(1,c-Math.min(d,2));b=0;xp=Math.max(5,(xp/(d+1)/5)*5);}
        return new Budget(c,b,xp,d==0?60:Math.max(10,60/(d+1)));
    }
    public static String bonusGrade(int players,int deaths,int roll) {
        if(deaths>0)return "";
        int size=Math.clamp(players,1,VortexProgress.MAX_PARTICIPANTS);
        return roll<5+size?"a_plus":roll<35+size*4?"a":"";
    }
    private static void extra(ServerPlayer player,net.minecraft.world.item.Item item) {
        var stack=new ItemStack(item);
        if(!player.getInventory().add(stack)){var drop=player.drop(stack,false);if(drop!=null)drop.setTarget(player.getUUID());}
        player.displayClientMessage(Component.translatable("message.spectrobesreforged.vortex.extra",item.getDescription()),false);
    }
    public static void give(ServerPlayer player,VortexProgress raid){
        var member=raid.participant(player.getUUID());
        var reward=budget(raid.playerCount(),member.deaths());
        for(int tier=0;tier<2;tier++)for(int i=0;i<(tier==0?reward.c():reward.b());i++){
            int kind=player.getRandom().nextInt(3);
            var item=tier==0?(kind==0?ModItems.POWER_C.get():kind==1?ModItems.DEFENSE_C.get():ModItems.HEALTH_C.get())
                    :(kind==0?ModItems.POWER_B.get():kind==1?ModItems.DEFENSE_B.get():ModItems.HEALTH_B.get());
            var stack=new ItemStack(item);
            if(!player.getInventory().add(stack)){var dropped=player.drop(stack,false);if(dropped!=null)dropped.setTarget(player.getUUID());}
        }
        String bonus=bonusGrade(raid.playerCount(),member.deaths(),player.getRandom().nextInt(1000));
        if(!bonus.isEmpty()){
            int kind=player.getRandom().nextInt(3);
            var extra=bonus.equals("a_plus")?(kind==0?ModItems.POWER_A_PLUS.get():kind==1?ModItems.DEFENSE_A_PLUS.get():ModItems.HEALTH_A_PLUS.get())
                    :(kind==0?ModItems.POWER_A.get():kind==1?ModItems.DEFENSE_A.get():ModItems.HEALTH_A.get());
            extra(player,extra);
        }
        if(member.deaths()==0&&player.getRandom().nextInt(10)==0)
            extra(player,com.jvprz.spectrobesreforged.common.registry.ModMineralLoot.SEEDS.get(new String[]{"hp","atk","def"}[player.getRandom().nextInt(3)]).get());
        if(member.deaths()==0&&player.getRandom().nextInt(100)==0)
            extra(player,com.jvprz.spectrobesreforged.common.registry.ModMineralLoot.EVOLVE.get("m").get());
        player.giveExperiencePoints(reward.xp());
        ModProgression.award(player,"seal_vortex");SpectrobeTraining.victory(player,reward.trainingXp());
        player.displayClientMessage(Component.translatable(member.deaths()>0?"message.spectrobesreforged.vortex.reward_reduced":"message.spectrobesreforged.vortex.reward",reward.c(),reward.b(),reward.xp()),false);
        player.serverLevel().playSound(null,player.blockPosition(),ModSounds.BATTLE_END.get(),SoundSource.PLAYERS,.9F,1);
    }
}
