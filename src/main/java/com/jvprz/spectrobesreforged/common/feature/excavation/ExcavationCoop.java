package com.jvprz.spectrobesreforged.common.feature.excavation;

import java.util.*;

/** Session-only cursors and short tool leases. Never serialized onto the specimen. */
public final class ExcavationCoop {
    public static final int MAX_VIEWERS=16,LEASE_TICKS=10;
    public record Cursor(UUID player,String name,int x,int y,int tool,boolean using) {}
    private record Lease(UUID player,long until) {}
    private final Map<UUID,Cursor> cursors=new LinkedHashMap<>();
    private final Map<Integer,Lease> leases=new HashMap<>();
    private final Map<UUID,Long> strokes=new HashMap<>();
    public boolean add(UUID id,String name){
        if(!cursors.containsKey(id)&&cursors.size()>=MAX_VIEWERS)return false;
        cursors.putIfAbsent(id,new Cursor(id,name,-1,-1,0,false));return true;
    }
    public boolean contains(UUID id){return cursors.containsKey(id);}
    public Set<UUID> viewers(){return Set.copyOf(cursors.keySet());}
    public static boolean validTool(int tool){return switch(tool){case 0,1,4,5,6,7,8,9,12,13,14->true;default->false;};}
    public UUID owner(int tool,long now){var lease=leases.get(tool);return lease!=null&&lease.until()>now?lease.player():null;}
    public boolean acquire(UUID id,int tool,long now){
        if(!cursors.containsKey(id)||!validTool(tool))return false;
        leases.entrySet().removeIf(e->e.getValue().player().equals(id)&&e.getKey()!=tool);
        UUID other=owner(tool,now);if(other!=null&&!other.equals(id))return false;
        leases.put(tool,new Lease(id,now+LEASE_TICKS));return true;
    }
    public void touch(UUID id,int x,int y,int tool,boolean using,long now){
        var old=cursors.get(id);if(old==null||!validTool(tool))return;
        boolean inside=x>=0&&x<160&&y>=0&&y<160;
        boolean active=inside&&using&&acquire(id,tool,now);
        if(!inside||!using)releaseTools(id);
        cursors.put(id,new Cursor(id,old.name(),inside?x:-1,inside?y:-1,tool,active));
    }
    public boolean stroke(UUID id,int tool,long now){
        Long last=strokes.get(id);
        if(last!=null&&now>=last&&now-last<2)return false;
        if(!acquire(id,tool,now))return false;
        strokes.put(id,now);return true;
    }
    public void releaseTools(UUID id){leases.entrySet().removeIf(e->e.getValue().player().equals(id));}
    public void leave(UUID id){cursors.remove(id);strokes.remove(id);releaseTools(id);}
    public void releaseAll(){leases.clear();}
    public void clear(){cursors.clear();leases.clear();strokes.clear();}
    public List<Cursor> snapshot(long now){
        return cursors.values().stream().map(c->new Cursor(c.player(),c.name(),c.x(),c.y(),c.tool(),c.using()&&c.player().equals(owner(c.tool(),now)))).toList();
    }
}
