package com.jvprz.spectrobesreforged.common.feature.vortex;

import java.util.*;

/** Saved raid ledger: identities, deaths and reward claims never reset on leaving the area. */
public final class VortexProgress {
    public enum Status { ACTIVE, COMPLETED, CANCELLED }
    public static final int ABSENCE_LIMIT=400, DURATION_LIMIT=12000, MAX_PARTICIPANTS=16;
    public record Participant(UUID id,int deaths,boolean fallen,boolean claimed) {
        public Participant { Objects.requireNonNull(id);deaths=Math.clamp(deaths,0,1000); }
    }
    private final UUID owner;
    private final Set<UUID> members=new LinkedHashSet<>(),defeated=new HashSet<>();
    private final Map<UUID,Participant> participants=new LinkedHashMap<>();
    private Status status=Status.ACTIVE;
    private int elapsed,absent;
    public VortexProgress(UUID owner,Collection<UUID> wave){
        this.owner=Objects.requireNonNull(owner);
        if(!join(owner,wave))throw new IllegalArgumentException("A participant needs three distinct Krawl");
    }
    public UUID owner(){return owner;}
    public Set<UUID> members(){return Set.copyOf(members);}
    public Set<UUID> defeated(){return Set.copyOf(defeated);}
    public Collection<Participant> participants(){return List.copyOf(participants.values());}
    public Participant participant(UUID id){return participants.get(id);}
    public boolean containsPlayer(UUID id){return participants.containsKey(id);}
    public int playerCount(){return participants.size();}
    public Status status(){return status;}
    public int elapsed(){return elapsed;}
    public int absent(){return absent;}
    public int remaining(){return members.size()-defeated.size();}
    public boolean join(UUID player,Collection<UUID> wave){
        if(status!=Status.ACTIVE||player==null||participants.containsKey(player)||participants.size()>=MAX_PARTICIPANTS
                ||wave.size()!=3||new HashSet<>(wave).size()!=3||wave.stream().anyMatch(Objects::isNull)||wave.stream().anyMatch(members::contains))return false;
        participants.put(player,new Participant(player,0,false,false));members.addAll(wave);return true;
    }
    public boolean died(UUID player){
        var p=participants.get(player);
        if(status!=Status.ACTIVE||p==null||p.fallen())return false;
        participants.put(player,new Participant(player,p.deaths()+1,true,false));
        if(participants.values().stream().allMatch(Participant::fallen))cancel();
        return true;
    }
    public boolean defeated(UUID mob){return status==Status.ACTIVE&&members.contains(mob)&&defeated.add(mob);}
    public void advance(int ticks,boolean ownerNearby){advance(ticks,ownerNearby?Set.of(owner):Set.of());}
    public void advance(int ticks,Set<UUID> livingNearby){
        if(status!=Status.ACTIVE||ticks<=0)return;
        boolean present=false;
        for(UUID id:livingNearby){var p=participants.get(id);if(p!=null){present=true;if(p.fallen())participants.put(id,new Participant(id,p.deaths(),false,p.claimed()));}}
        elapsed=Math.min(DURATION_LIMIT,elapsed+Math.min(ticks,DURATION_LIMIT));
        absent=present?0:Math.min(ABSENCE_LIMIT,absent+Math.min(ticks,ABSENCE_LIMIT));
        if(absent>=ABSENCE_LIMIT||elapsed>=DURATION_LIMIT)cancel();
        else if(present&&remaining()==0)status=Status.COMPLETED;
    }
    public boolean claim(UUID id){
        var p=participants.get(id);
        if(status!=Status.COMPLETED||p==null||p.claimed())return false;
        participants.put(id,new Participant(id,p.deaths(),p.fallen(),true));return true;
    }
    public void cancel(){if(status==Status.ACTIVE)status=Status.CANCELLED;}
    void restore(Status status,Collection<UUID> defeated,int elapsed,int absent){
        this.status=status;this.defeated.addAll(defeated);this.defeated.retainAll(members);
        this.elapsed=Math.clamp(elapsed,0,DURATION_LIMIT);this.absent=Math.clamp(absent,0,ABSENCE_LIMIT);
    }
    static VortexProgress restored(UUID owner,Collection<UUID> members,Collection<UUID> defeated,
            Collection<Participant> players,Status status,int elapsed,int absent){
        if(players.isEmpty()||players.size()>MAX_PARTICIPANTS||members.size()!=players.size()*3
                ||new HashSet<>(members).size()!=members.size()||players.stream().map(Participant::id).distinct().count()!=players.size()
                ||players.stream().noneMatch(p->p.id().equals(owner)))throw new IllegalArgumentException("Invalid saved raid");
        if(status==Status.COMPLETED&&!new HashSet<>(defeated).containsAll(members))throw new IllegalArgumentException("Incomplete raid cannot authorize rewards");
        var result=new VortexProgress(owner,new ArrayList<>(members).subList(0,3));
        result.members.clear();result.members.addAll(members);result.participants.clear();
        players.forEach(p->result.participants.put(p.id(),p));result.restore(status,defeated,elapsed,absent);return result;
    }
}
