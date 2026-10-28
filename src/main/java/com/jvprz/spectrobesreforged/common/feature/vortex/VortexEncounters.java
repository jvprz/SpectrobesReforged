package com.jvprz.spectrobesreforged.common.feature.vortex;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.*;

/** Persistent participant identities and terminal claim markers prevent re-entry duplication. */
public final class VortexEncounters extends SavedData {
    private static final Factory<VortexEncounters> FACTORY=new Factory<>(VortexEncounters::new,VortexEncounters::load,null);
    private final Map<UUID,VortexProgress> encounters=new HashMap<>();
    public static VortexEncounters get(ServerLevel level){return level.getDataStorage().computeIfAbsent(FACTORY,"spectrobesreforged_vortices");}
    public VortexProgress find(UUID id){return encounters.get(id);}
    public boolean hasActiveEnemies(UUID player){return encounters.values().stream().anyMatch(p->p.containsPlayer(player)&&p.status()==VortexProgress.Status.ACTIVE&&p.remaining()>0);}
    public boolean hasActiveRaid(UUID player){return encounters.values().stream().anyMatch(p->p.containsPlayer(player)&&p.status()==VortexProgress.Status.ACTIVE);}
    public static boolean companionSuppressed(ServerPlayer player){
        for(var level:player.server.getAllLevels())if(get(level).hasActiveEnemies(player.getUUID()))return true;return false;
    }
    public static boolean inActiveRaid(ServerPlayer player){
        for(var level:player.server.getAllLevels())if(get(level).hasActiveRaid(player.getUUID()))return true;return false;
    }
    public boolean begin(UUID id,VortexProgress progress){if(encounters.putIfAbsent(id,progress)!=null)return false;setDirty();return true;}
    public void defeated(UUID id,UUID mob){var p=find(id);if(p!=null&&p.defeated(mob))setDirty();}
    public void playerDied(ServerPlayer player,ServerLevel level){
        for(var entry:encounters.entrySet())if(entry.getValue().died(player.getUUID())){
            setDirty();
            if(entry.getValue().status()==VortexProgress.Status.CANCELLED
                    &&level.getEntity(entry.getKey()) instanceof com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlVortexEntity vortex)vortex.finishEncounter(entry.getValue());
        }
    }
    public static void payPending(ServerPlayer player){
        if(!player.isAlive()||player.isSpectator())return;
        for(var level:player.server.getAllLevels()){
            var data=get(level);
            for(var raid:data.encounters.values())if(raid.claim(player.getUUID())){data.setDirty();VortexRewards.give(player,raid);}
        }
    }
    static VortexEncounters load(CompoundTag tag,HolderLookup.Provider registries){
        var data=new VortexEncounters();var entries=tag.getList("Encounters",Tag.TAG_COMPOUND);
        for(int i=0;i<entries.size();i++)try{
            var e=entries.getCompound(i);var mobs=e.getList("Members",Tag.TAG_COMPOUND);
            var members=new ArrayList<UUID>();var defeated=new ArrayList<UUID>();
            for(int j=0;j<mobs.size();j++){var m=mobs.getCompound(j);var id=m.getUUID("Id");members.add(id);if(m.getBoolean("Defeated"))defeated.add(id);}
            UUID owner=e.getUUID("Owner");var status=VortexProgress.Status.valueOf(e.getString("Status"));
            var players=new ArrayList<VortexProgress.Participant>();
            if(e.contains("Participants",Tag.TAG_LIST)){
                var list=e.getList("Participants",Tag.TAG_COMPOUND);
                for(int j=0;j<list.size();j++){var p=list.getCompound(j);players.add(new VortexProgress.Participant(p.getUUID("Id"),p.getInt("Deaths"),p.getBoolean("Fallen"),p.getBoolean("Claimed")));}
            }else players.add(new VortexProgress.Participant(owner,0,false,status==VortexProgress.Status.COMPLETED));
            var progress=VortexProgress.restored(owner,members,defeated,players,status,e.getInt("Elapsed"),e.getInt("Absent"));
            data.encounters.put(e.getUUID("Id"),progress);
        }catch(IllegalArgumentException ignored){ /* Malformed entries never authorize rewards. */ }
        return data;
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider registries){
        var entries=new ListTag();
        encounters.forEach((id,p)->{
            var e=new CompoundTag();e.putUUID("Id",id);e.putUUID("Owner",p.owner());e.putString("Status",p.status().name());e.putInt("Elapsed",p.elapsed());e.putInt("Absent",p.absent());
            var members=new ListTag();for(UUID mob:p.members()){var m=new CompoundTag();m.putUUID("Id",mob);m.putBoolean("Defeated",p.defeated().contains(mob));members.add(m);}e.put("Members",members);
            var players=new ListTag();for(var member:p.participants()){var a=new CompoundTag();a.putUUID("Id",member.id());a.putInt("Deaths",member.deaths());a.putBoolean("Fallen",member.fallen());a.putBoolean("Claimed",member.claimed());players.add(a);}e.put("Participants",players);entries.add(e);
        });tag.put("Encounters",entries);return tag;
    }
}
