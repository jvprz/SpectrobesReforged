package com.jvprz.spectrobesreforged.common.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;

/** The grade remains geological data; the visible ore identifies its elemental family. */
public final class ElementalMineralOreBlock extends Block {
    public static final IntegerProperty GRADE=IntegerProperty.create("grade",0,3);
    private final String family,host;
    public ElementalMineralOreBlock(String family,String host,Properties properties){
        super(properties);this.family=family;this.host=host;
        registerDefaultState(stateDefinition.any().setValue(GRADE,0));
    }
    public String family(){return family;}
    public String host(){return host;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(GRADE);}
    @Override public void spawnAfterBreak(BlockState state,ServerLevel level,BlockPos pos,ItemStack tool,boolean experience){
        super.spawnAfterBreak(state,level,pos,tool,experience);
        var color=switch(family){case "coronium"->new Vector3f(.95f,.16f,.12f);case "aurorium"->new Vector3f(.2f,.9f,.25f);default->new Vector3f(.2f,.65f,1f);};
        level.sendParticles(new DustParticleOptions(color,1f),pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,8,.35,.35,.35,0);
    }
}
