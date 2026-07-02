package com.jvprz.spectrobesreforged.common.content.block;

import com.jvprz.spectrobesreforged.common.feature.mineral.MineralTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class MineralOreBlock extends Block {

    private final MineralTier tier;

    public MineralOreBlock(MineralTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos,
                                net.minecraft.world.item.ItemStack stack,
                                boolean dropExperience) {

        super.spawnAfterBreak(state, level, pos, stack, dropExperience);

        spawnParticles(level, pos);
    }

    private void spawnParticles(ServerLevel level, BlockPos pos) {
        Vector3f color = switch (tier) {
            case C -> new Vector3f(1.0f, 0.9f, 0.1f);     // amarillo
            case B -> new Vector3f(0.2f, 1.0f, 0.3f);     // verde
            case A -> new Vector3f(1.0f, 0.2f, 0.7f);     // rosa
            case A_PLUS -> new Vector3f(0.7f, 0.2f, 1.0f); // morado
        };

        RandomSource random = level.random;

        int count = switch (tier) {
            case C -> 8;
            case B -> 10;
            case A -> 12;
            case A_PLUS -> 16; // más épico
        };

        for (int i = 0; i < count; i++) {
            level.sendParticles(
                    new DustParticleOptions(color, 1.0f),
                    pos.getX() + random.nextDouble(),
                    pos.getY() + random.nextDouble(),
                    pos.getZ() + random.nextDouble(),
                    1,
                    0, 0, 0,
                    0
            );
        }
    }
}