package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.feature.training.MineralTraining;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import java.util.*;

public final class TrainingMineralItem extends MineralItem {
    public enum Kind { ELEMENTAL, SEED, EVOLVE }
    private final Kind kind;
    private final String family,grade;
    public TrainingMineralItem(Properties properties,Kind kind,String family,String grade) {
        super(properties,null,false,null,false,null,false,"tooltip.spectrobesreforged.new_mineral."+kind.name().toLowerCase(Locale.ROOT));
        this.kind=kind;this.family=family;this.grade=grade;
    }
    @Override public boolean feedTo(ServerPlayer player,UUID id,ItemStack stack,SpectrobeEntity entity) {
        if(stack.isEmpty()||stack.getItem()!=this)return false;
        var entry=player.getData(ModAttachments.PRIZMOD.get()).findAnywhere(id).orElse(null);
        if(entry==null||entry.isDead())return false;
        var species=SpectrobeSpeciesRegistry.getByKey(entry.species());if(species==null)return false;
        var next=switch(kind){
            case ELEMENTAL->MineralTraining.elemental(entry,species,family,grade);
            case SEED->MineralTraining.seed(entry,species,family);
            case EVOLVE->MineralTraining.evolve(entry,species,family);
        };
        if(next.equals(entry)){
            player.displayClientMessage(Component.translatable(kind==Kind.SEED?"message.spectrobesreforged.seed_limit":"message.spectrobesreforged.no_effect"),true);
            return false;
        }
        return finishFeeding(player,entry,next,stack,entity);
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.spectrobesreforged.new_mineral."+kind.name().toLowerCase(Locale.ROOT)));
        if(kind==Kind.ELEMENTAL)lines.add(Component.translatable("tooltip.spectrobesreforged.training_xp",
                com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.mineralPercent(family+"_"+grade)));
        if(kind==Kind.EVOLVE)lines.add(Component.translatable("tooltip.spectrobesreforged.evolve_"+family));
        if(kind==Kind.SEED)lines.add(Component.translatable("tooltip.spectrobesreforged.seed_"+family));
    }
}
