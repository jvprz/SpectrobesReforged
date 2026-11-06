package com.jvprz.spectrobesreforged.common.feature.training;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.*;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.*;

/** Deterministic level growth and small integrity bonuses; evolution is deliberately separate. */
public final class SpectrobeLevels {
    public static final int MAX_LEVEL=100;
    private SpectrobeLevels(){}
    public static int required(int level){return level>=MAX_LEVEL?0:100+25*(Math.max(1,level)-1);}
    public static int qualityPercent(int quality){return quality>=95?3:quality>=85?2:quality>=70?1:0;}
    public static int boosted(int value,int quality){return value+(int)((long)Math.max(0,value)*qualityPercent(quality)/100);}
    public static int intrinsic(StatGrowth stat,int level,int quality){
        int base=Math.max(0,stat.base()),max=Math.max(base,stat.max());
        return boosted(base+(int)((long)(max-base)*(Math.clamp(level,1,100)-1)/99),quality);
    }
    public static SpectrobeEntry fromFossil(SpectrobeEntry entry,int integrity,boolean crown){
        var progress=new SpectrobeProgress(0,crown,integrity,0);
        int hp=boosted(entry.hp(),integrity),atk=boosted(entry.atk(),integrity),def=boosted(entry.def(),integrity);
        return entry.withProgress(progress).withStats(hp,hp,atk,def);
    }
    public static SpectrobeEntry award(SpectrobeEntry entry,int amount,SpectrobeSpecies species){
        if(amount<=0||entry.level()>=MAX_LEVEL)return entry;
        int level=entry.level();long xp=(long)entry.xp()+amount;
        while(level<MAX_LEVEL&&xp>=required(level)){xp-=required(level);level++;}
        if(level==MAX_LEVEL)xp=0;
        var updated=entry.withLevelProgress(level,entry.progress().withXp((int)xp));
        if(species==null||level==entry.level())return updated;
        int quality=entry.excavationQuality();
        int hp=grow(entry.hp(),species.stats().hp(),entry.level(),level,quality);
        int atk=grow(entry.atk(),species.stats().attack(),entry.level(),level,quality);
        int def=grow(entry.def(),species.stats().defense(),entry.level(),level,quality);
        int cur=entry.isDead()?0:Math.min(hp,entry.hpCur()+Math.max(0,hp-entry.hp()));
        return updated.withStats(hp,cur,atk,def);
    }
    private static int grow(int current,StatGrowth stat,int before,int after,int quality){
        int delta=intrinsic(stat,after,quality)-intrinsic(stat,before,quality);
        // Never reduce a legacy stat that already exceeds today's species cap.
        return Math.max(current,Math.min(boosted(stat.max(),quality),current+Math.max(0,delta)));
    }
    public static int mineralPercent(String id){
        if(id.equals("diamond")||id.startsWith("evolve")||id.startsWith("power_")||id.startsWith("defense_")||id.startsWith("health_"))return 0;
        return id.endsWith("_s")?100:id.endsWith("_a_plus")?60:id.endsWith("_a")?40:id.endsWith("_b")?20:id.endsWith("_c")?10:25;
    }
    // Balance anchors: level -> fraction of the mineral's base percentage.
    private static final int[] MINERAL_LEVELS={1,25,50,75,100};
    private static final double[] MINERAL_EFFICIENCY={1.0,.80,.60,.45,.35};
    public static double mineralEfficiency(int level){
        level=Math.clamp(level,1,MAX_LEVEL);
        for(int i=1;i<MINERAL_LEVELS.length;i++)if(level<=MINERAL_LEVELS[i]){
            double progress=(level-MINERAL_LEVELS[i-1])/(double)(MINERAL_LEVELS[i]-MINERAL_LEVELS[i-1]);
            return MINERAL_EFFICIENCY[i-1]+progress*(MINERAL_EFFICIENCY[i]-MINERAL_EFFICIENCY[i-1]);
        }
        return MINERAL_EFFICIENCY[MINERAL_EFFICIENCY.length-1];
    }
    public static int mineralXp(SpectrobeEntry entry,int percent){
        if(entry.level()>=MAX_LEVEL||percent<=0)return 0;
        int level=entry.level(),current=entry.xp(),total=0;double remaining=percent;
        while(remaining>0&&level<MAX_LEVEL){
            int needed=required(level)-current;
            double efficiency=mineralEfficiency(level);
            // Spend base-percentage budget at the current level's efficiency. When a mineral
            // crosses a level boundary, its remainder uses the new level's lower efficiency.
            double cost=needed*100.0/required(level)/efficiency;
            if(remaining+1e-9<cost){total+=(int)Math.ceil(required(level)*remaining*efficiency/100.0-1e-9);break;}
            total+=needed;remaining-=cost;level++;current=0;
        }
        return total;
    }
}
