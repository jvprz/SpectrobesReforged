package com.jvprz.spectrobesreforged.common.feature.prizmod.data;

import java.util.UUID;

/** Validate the whole exchange before changing storage indices or companion slots. */
public final class PrizmodTransfers {
    private PrizmodTransfers(){}
    private static SpectrobeEntry get(PrizmodData data,int type,int index){return switch(type){case 0->data.getBoxAt(index);case 1->data.getTeamSlot(index).orElse(null);case 2->data.getBabySlot().orElse(null);default->null;};}
    private static boolean position(int type,int index){return type==0?index>=0:type==1?index>=0&&index<6:type==2&&index==0;}
    private static boolean accepts(int type,SpectrobeEntry entry){return entry==null||type==0||(type==2?entry.isChild():!entry.isChild());}
    public static boolean move(PrizmodData data,int from,int fromIndex,int to,int toIndex,UUID expected){
        if(!position(from,fromIndex)||!position(to,toIndex)||(from==to&&fromIndex==toIndex))return false;
        var moving=get(data,from,fromIndex);
        if(moving==null||expected==null||!expected.equals(moving.id())||!accepts(to,moving))return false;
        if(from==0&&to==0)return data.rearrangeBox(fromIndex,toIndex);
        var displaced=get(data,to,toIndex);if(!accepts(from,displaced))return false;
        take(data,from,fromIndex);if(displaced!=null)take(data,to,toIndex);
        if(displaced!=null)put(data,from,fromIndex,displaced);put(data,to,toIndex,moving);return true;
    }
    private static void take(PrizmodData data,int type,int index){switch(type){case 0->data.removeBoxAt(index);case 1->data.setTeamSlot(index,null);case 2->data.setBabySlot(null);}}
    private static void put(PrizmodData data,int type,int index,SpectrobeEntry entry){switch(type){case 0->data.insertBoxAt(Math.min(index,data.getBox().size()),entry);case 1->data.setTeamSlot(index,entry);case 2->data.setBabySlot(entry);}}
}
