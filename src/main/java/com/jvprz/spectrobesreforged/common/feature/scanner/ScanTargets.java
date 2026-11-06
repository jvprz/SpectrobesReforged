package com.jvprz.spectrobesreforged.common.feature.scanner;

import com.jvprz.spectrobesreforged.common.content.block.FossilOreBlock;
import com.jvprz.spectrobesreforged.common.content.block.MineralOreBlock;
import com.jvprz.spectrobesreforged.common.content.block.ElementalMineralOreBlock;
import net.minecraft.world.level.block.state.BlockState;

/** One classification for both the incremental search and its visual/audio hints. */
public final class ScanTargets {
    public enum Kind { FOSSIL, MINERAL }
    private ScanTargets(){}
    public static Kind kind(BlockState state){
        if(state.getBlock() instanceof FossilOreBlock)return Kind.FOSSIL;
        if(state.getBlock() instanceof MineralOreBlock||state.getBlock() instanceof ElementalMineralOreBlock)return Kind.MINERAL;
        return null;
    }
}
