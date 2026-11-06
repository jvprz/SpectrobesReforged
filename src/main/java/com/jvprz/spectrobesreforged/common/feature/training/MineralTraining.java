package com.jvprz.spectrobesreforged.common.feature.training;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeSpecies;

public final class MineralTraining {
    private MineralTraining() {}
    public static SpectrobeEntry elemental(SpectrobeEntry entry,SpectrobeSpecies species,String family,String grade) {
        String expected=switch(species.type()){case CORONA->"coronium";case AURORA->"aurorium";case FLASH->"flashium";};
        int percent=SpectrobeLevels.mineralPercent(family+"_"+grade);
        if(!expected.equals(family))percent/=2;
        int xp=SpectrobeLevels.mineralXp(entry,percent);
        return xp<=0?entry:SpectrobeLevels.award(entry.withMineralsFed(entry.mineralsFed()+1),xp,species);
    }
    public static SpectrobeEntry seed(SpectrobeEntry e,SpectrobeSpecies s,String stat) {
        if(e.progress().seeds()>=Math.min(e.level(),SpectrobeLevels.MAX_LEVEL))return e;
        int quality=e.excavationQuality();
        int hp=e.hp(),atk=e.atk(),def=e.def();
        switch(stat){
            case "hp"->hp=Math.max(hp,Math.min(SpectrobeLevels.boosted(s.stats().hp().max(),quality),hp+10));
            case "atk"->atk=Math.max(atk,Math.min(SpectrobeLevels.boosted(s.stats().attack().max(),quality),atk+1));
            case "def"->def=Math.max(def,Math.min(SpectrobeLevels.boosted(s.stats().defense().max(),quality),def+1));
            default->{return e;}
        }
        if(hp==e.hp()&&atk==e.atk()&&def==e.def())return e;
        return e.withStats(hp,e.isDead()?0:Math.min(hp,e.hpCur()+hp-e.hp()),atk,def)
                .withMineralBonuses(e.mineralHpBonus()+hp-e.hp(),e.mineralAtkBonus()+atk-e.atk(),e.mineralDefBonus()+def-e.def())
                .withMineralsFed(e.mineralsFed()+1).withProgress(e.progress().withSeeds(e.progress().seeds()+1));
    }
    public static SpectrobeEntry evolve(SpectrobeEntry e,SpectrobeSpecies s,String requirement) {
        var rules=s.evolution();if(!rules.canEvolve())return e;
        var result=switch(requirement){
            case "m"->rules.mineralsRequired()>e.mineralsFed()?e.withMineralsFed(rules.mineralsRequired()):e;
            case "b"->rules.battlesRequired()>e.battlesWon()?e.withProgress(e.progress().withBattles(rules.battlesRequired())):e;
            case "l"->{
                int level=rules.levelRequired(),xp=0;
                if(level<=e.level()){yield e;}
                for(int current=e.level();current<level;current++)xp+=SpectrobeLevels.required(current);
                yield SpectrobeLevels.award(e,Math.max(0,xp-e.xp()),s);
            }
            default->e;
        };
        if(result==e)return e;
        return requirement.equals("m")?result:result.withMineralsFed(e.mineralsFed()+1);
    }
}
