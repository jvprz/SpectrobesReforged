package com.jvprz.spectrobesreforged.common.feature.training;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.*;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.*;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class MineralTrainingTest {
    private SpectrobeEntry entry(){return new SpectrobeEntry(UUID.randomUUID(),"komainu",0,"CHILD",1,120,100,65,55,0,0,0,0);}
    private SpectrobeSpecies species(int level,int battles){return new SpectrobeSpecies(1,"komainu",SpectrobeType.CORONA,SpectrobeStage.CHILD,3,
            new SpectrobeStats(new StatGrowth(120,200,1),new StatGrowth(65,125,1),new StatGrowth(55,105,1),0,0,null),
            new SpectrobeSpecies.EvolutionData("komanoto",6,level,battles),List.of());}
    @Test void elementalFoodUsesAffinityAndDoesNotDirectlyChangeStats(){
        var e=entry();var s=species(0,0);
        var matched=MineralTraining.elemental(e,s,"coronium","c");
        var other=MineralTraining.elemental(e,s,"flashium","c");
        assertEquals(10,matched.xp());assertEquals(5,other.xp());
        assertEquals(e.atk(),matched.atk());assertEquals(e.hp(),matched.hp());
        assertEquals(1,matched.mineralsFed());
        for(String family:List.of("power","defense","health"))for(String grade:List.of("c","b","a","a_plus"))assertEquals(0,SpectrobeLevels.mineralPercent(family+"_"+grade));
    }
    @Test void seedsShareAllowanceAndPreserveItAcrossCopiesAndSerialization(){
        var e=entry();var s=species(0,0);
        var fed=MineralTraining.seed(e,s,"atk");
        assertEquals(66,fed.atk());assertEquals(1,fed.progress().seeds());assertEquals(0,fed.xp());
        assertEquals(fed,MineralTraining.seed(fed,s,"hp"));
        var grown=SpectrobeLevels.award(fed,100,s);
        var fedAgain=MineralTraining.seed(grown,s,"hp");assertEquals(2,fedAgain.progress().seeds());
        var save=SpectrobeEntry.CODEC.encodeStart(JsonOps.INSTANCE,fedAgain).getOrThrow();
        assertEquals(fedAgain,SpectrobeEntry.CODEC.parse(JsonOps.INSTANCE,save).getOrThrow());
        assertEquals(2,fedAgain.progress().withCrown().withCooperative().won().withXp(2).seeds());
        var capped=e.withStats(200,200,125,105);assertEquals(capped,MineralTraining.seed(capped,s,"hp"));
    }
    @Test void evolveMineralsSatisfyOnlyTheirDefinedRequirementWithoutChangingSpecies(){
        var e=entry();var s=species(10,5);
        var m=MineralTraining.evolve(e,s,"m");assertEquals(6,m.mineralsFed());assertEquals(1,m.level());
        assertEquals(m,MineralTraining.evolve(m,s,"m"));
        var l=MineralTraining.evolve(e,s,"l");assertEquals(10,l.level());assertEquals(0,l.battlesWon());
        var b=MineralTraining.evolve(e,s,"b");assertEquals(5,b.battlesWon());assertEquals(1,b.level());
        for(var result:List.of(m,l,b)){assertEquals(e.species(),result.species());assertEquals(e.stage(),result.stage());}
        assertEquals(e,MineralTraining.evolve(e,species(0,0),"b"));
        assertEquals(e,MineralTraining.evolve(e,species(0,0),"l"));
    }
}
