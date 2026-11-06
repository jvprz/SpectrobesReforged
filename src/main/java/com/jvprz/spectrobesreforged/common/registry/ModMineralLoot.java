package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.common.content.item.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.*;
import java.util.*;

/** Ordered expansion groups, kept separate from the legacy mineral registrations. */
public final class ModMineralLoot {
    private ModMineralLoot() {}
    public static final List<String> GRADES=List.of("c","b","a","a_plus","s");
    public static final List<String> CUBES=List.of("alpha","beta","gamma","delta","epsilon","zeta","eta","theta","iota","kappa","lambda","mu","nu","xi","omicron","pi","rho","sigma","tau","upsilon");
    public static final List<String> MYSTERIES=List.of("skull","plate","piggy_bank","seed_pod","saucer","chest","question_mark","gift");
    public static final Map<String,DeferredItem<Item>> ELEMENTALS=new LinkedHashMap<>(),SEEDS=new LinkedHashMap<>(),EVOLVE=new LinkedHashMap<>(),MATERIALS=new LinkedHashMap<>(),LOOT=new LinkedHashMap<>();
    public static void register(DeferredRegister.Items items) {
        for(String family:List.of("coronium","aurorium","flashium"))for(String grade:GRADES) {
            String id=family+"_"+grade;
            ELEMENTALS.put(id,items.register(id,()->new TrainingMineralItem(new Item.Properties().rarity(grade.equals("s")?Rarity.EPIC:grade.startsWith("a")?Rarity.RARE:Rarity.COMMON),TrainingMineralItem.Kind.ELEMENTAL,family,grade)));
        }
        for(String stat:List.of("hp","atk","def"))SEEDS.put(stat,items.register(stat+"_seed",()->new TrainingMineralItem(new Item.Properties().rarity(Rarity.RARE),TrainingMineralItem.Kind.SEED,stat,"")));
        for(String kind:List.of("m","l","b"))EVOLVE.put(kind,items.register("evolve_"+kind,()->new TrainingMineralItem(new Item.Properties().rarity(Rarity.EPIC),TrainingMineralItem.Kind.EVOLVE,kind,"")));
        for(String id:List.of("metalium","titanium","marble"))MATERIALS.put(id,items.register(id,()->new PreparedLootItem()));
        for(String cube:CUBES)LOOT.put("cube_"+cube,items.register("cube_"+cube,()->new PreparedLootItem(new net.minecraft.world.item.Item.Properties().component(net.minecraft.core.component.DataComponents.ENCHANTMENT_GLINT_OVERRIDE,true),false)));
        for(String id:List.of("relic_crystal","neo"))LOOT.put(id,items.register(id,()->new PreparedLootItem(true)));
        for(String shape:MYSTERIES){String id="mystery_stone_"+shape;LOOT.put(id,items.register(id,()->new PreparedLootItem(true)));}
    }
}

