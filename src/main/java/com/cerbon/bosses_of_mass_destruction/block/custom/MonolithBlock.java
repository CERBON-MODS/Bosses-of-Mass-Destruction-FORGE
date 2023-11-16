package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MonolithBlock extends BaseEntityBlock {
    private static final VoxelShape xAxisShape = box(3.5, 0.0, 1.5, 12.5, 16.0, 14.5);
    private static final VoxelShape zAxisShape = box(1.5, 0.0, 3.5, 14.5, 16.0, 12.5);

    public MonolithBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.bosses_of_mass_destruction.monolith_block.tooltip_0").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.bosses_of_mass_destruction.monolith_block.tooltip_1").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, @NotNull BlockState state) {
        return new MonolithBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BMDBlockEntities.MONOLITH_BLOCK_ENTITY.get(), ChunkCacheBlockEntity::tick);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(HorizontalDirectionalBlock.FACING).getAxis() == Direction.Axis.X ? xAxisShape : zAxisShape;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, @NotNull BlockState newState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos posFrom) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        BlockState airState = Blocks.AIR.defaultBlockState();

        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)){
            if (newState.is(this) && newState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != doubleBlockHalf)
                return state.setValue(HorizontalDirectionalBlock.FACING, newState.getValue(HorizontalDirectionalBlock.FACING));
            else return airState;
        } else {
            return super.updateShape(state, direction, newState, level, pos, posFrom);
        }
    }

    @Override
    public void playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide && player.isCreative())
            VanillaCopies.onBreakInCreative(level, pos, state, player);

        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();

        if (blockPos.getY() < 255 && ctx.getLevel().getBlockState(blockPos.above()).canBeReplaced(ctx))
            return getStateDefinition().any().setValue(HorizontalDirectionalBlock.FACING, ctx.getHorizontalDirection())
                    .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
        else return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockPos = pos.below();
        BlockState blockState = level.getBlockState(blockPos);

        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
            return blockState.isFaceSturdy(level, blockPos, Direction.UP);
        else
            return blockState.is(this);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(
                HorizontalDirectionalBlock.FACING,
                rotation.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(HorizontalDirectionalBlock.FACING))).cycle(DoorBlock.HINGE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.DOUBLE_BLOCK_HALF, HorizontalDirectionalBlock.FACING);
    }

    public static float getExplosionPower(ServerLevel level, BlockPos pos, float power){
        ChunkPos chunkPos = new ChunkPos(pos);
        Optional<ChunkBlockCache> blockCache = BMDCapabilities.getChunkBlockCache(level);

        if (blockCache.isPresent()) {
            for (int x = chunkPos.x - 4; x <= chunkPos.x + 4; x++)
                for (int z = chunkPos.z - 4; z <= chunkPos.z + 4; z++) {
                    List<BlockPos> blocks = blockCache.get().getBlocksFromChunk(new ChunkPos(x, z), BMDBlocks.MONOLITH_BLOCK.get());
                    for (BlockPos blockPos : blocks) {
                        if (Math.abs(blockPos.getX() - pos.getX()) < 64 && Math.abs(blockPos.getY() - pos.getY()) < 64 && Math.abs(blockPos.getZ() - pos.getZ()) < 64) {
                            return power * 1.3f;
                        }
                    }
                }
        }

        return power;
    }
}
