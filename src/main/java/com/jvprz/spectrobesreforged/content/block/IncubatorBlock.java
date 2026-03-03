package com.jvprz.spectrobesreforged.content.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class IncubatorBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<IncubatorBlock> CODEC = simpleCodec(IncubatorBlock::new);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public IncubatorBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, BedPart.FOOT));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {

        Direction playerFacing = ctx.getHorizontalDirection();
        Direction left = playerFacing.getCounterClockWise();

        BlockPos footPos = ctx.getClickedPos();
        BlockPos headPos = footPos.relative(left);

        Level level = ctx.getLevel();

        if (!level.getBlockState(headPos).canBeReplaced(ctx)) return null;
        if (!level.getWorldBorder().isWithinBounds(headPos)) return null;

        return this.defaultBlockState()
                .setValue(FACING, playerFacing)
                .setValue(PART, BedPart.FOOT);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable net.minecraft.world.entity.LivingEntity placer,
                            ItemStack stack) {

        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide) return;

        Direction playerFacing = state.getValue(FACING);
        Direction left = playerFacing.getCounterClockWise();

        BlockPos headPos = pos.relative(left);
        BlockState headState = state.setValue(PART, BedPart.HEAD);

        level.setBlock(headPos, headState, Block.UPDATE_ALL);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {

        BedPart part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        Direction left = facing.getCounterClockWise();

        Direction expectedDir = (part == BedPart.FOOT) ? left : left.getOpposite();

        if (dir == expectedDir) {
            if (!neighbor.is(this) || neighbor.getValue(PART) == part) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {

        if (level.isClientSide) {
            return super.playerWillDestroy(level, pos, state, player);
        }

        BedPart part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        Direction left = facing.getCounterClockWise();

        BlockPos footPos = (part == BedPart.FOOT) ? pos : pos.relative(left.getOpposite());
        BlockPos headPos = (part == BedPart.FOOT) ? pos.relative(left) : pos;

        if (player.isCreative()) {
            level.setBlock(footPos, Blocks.AIR.defaultBlockState(),
                    Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            level.setBlock(headPos, Blocks.AIR.defaultBlockState(),
                    Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            return state;
        }

        if (part == BedPart.HEAD) {
            level.destroyBlock(footPos, true, player);
        } else {
            level.setBlock(headPos, Blocks.AIR.defaultBlockState(),
                    Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(FACING, PART);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }
}