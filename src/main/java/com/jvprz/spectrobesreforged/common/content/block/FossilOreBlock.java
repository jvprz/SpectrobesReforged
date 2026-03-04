package com.jvprz.spectrobesreforged.common.content.block;

import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeType;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;

public class FossilOreBlock extends Block {

    private final SpectrobeType type;

    public FossilOreBlock(SpectrobeType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos,
                                net.minecraft.world.item.ItemStack stack,
                                boolean dropExperience) {

        super.spawnAfterBreak(state, level, pos, stack, dropExperience);

        spawnParticles(level, pos);
    }

    private void spawnParticles(ServerLevel level, BlockPos pos) {
        Vector3f color = switch (type) {
            case CORONA -> new Vector3f(1.0f, 0.1f, 0.1f);  // rojo
            case AURORA -> new Vector3f(0.1f, 1.0f, 0.3f);  // verde
            case FLASH  -> new Vector3f(0.2f, 0.6f, 1.0f);  // azul
        };

        RandomSource random = level.random;

        for (int i = 0; i < 12; i++) {
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