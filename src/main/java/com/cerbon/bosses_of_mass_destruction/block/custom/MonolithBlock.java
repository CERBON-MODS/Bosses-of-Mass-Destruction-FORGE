package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class MonolithBlock extends ContainerBlock {
    private static final VoxelShape xAxisShape = box(3.5, 0.0, 1.5, 12.5, 16.0, 14.5);
    private static final VoxelShape zAxisShape = box(1.5, 0.0, 3.5, 14.5, 16.0, 12.5);

    public MonolithBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(HorizontalBlock.FACING, Direction.NORTH)
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable IBlockReader level, List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent("item.bosses_of_mass_destruction.monolith_block.tooltip_0").withStyle(TextFormatting.DARK_GRAY));
        tooltip.add(new TranslationTextComponent("item.bosses_of_mass_destruction.monolith_block.tooltip_1").withStyle(TextFormatting.DARK_GRAY));
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader blockReader) {
        return new MonolithBlockEntity();
    }

    @Override
    public @Nonnull BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.MODEL;
    }

//    @Nullable
//    @Override
//    public <T extends TileEntity> BlockEntityTicker<T> getTicker(@Nonnull World level, @Nonnull BlockState state, @Nonnull TileEntityType<T> blockEntityType) {
//        return createTickerHelper(blockEntityType, BMDBlockEntities.MONOLITH_BLOCK_ENTITY.get(), ChunkCacheBlockEntity::tick);
//    }

    @Override
    public @Nonnull VoxelShape getShape(BlockState state, @Nonnull IBlockReader level, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return state.getValue(HorizontalBlock.FACING).getAxis() == Direction.Axis.X ? xAxisShape : zAxisShape;
    }

    @Override
    public @Nonnull BlockState updateShape(BlockState state, Direction direction, @Nonnull BlockState newState, @Nonnull IWorld level, @Nonnull BlockPos pos, @Nonnull BlockPos posFrom) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        BlockState airState = Blocks.AIR.defaultBlockState();

        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)){
            if (newState.is(this) && newState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != doubleBlockHalf)
                return state.setValue(HorizontalBlock.FACING, newState.getValue(HorizontalBlock.FACING));
            else
                return airState;
        } else
            return super.updateShape(state, direction, newState, level, pos, posFrom);
    }

    @Override
    public void playerWillDestroy(World level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity player) {
        if (!level.isClientSide && player.isCreative()){
            VanillaCopiesServer.onBreakInCreative(level, pos, state, player);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();

        if (blockPos.getY() < 255 && ctx.getLevel().getBlockState(blockPos.above()).canBeReplaced(ctx))
            return getStateDefinition().any().setValue(HorizontalBlock.FACING, ctx.getHorizontalDirection())
                    .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
        else
            return null;
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
        BlockPos blockPos = pos.below();
        BlockState blockState = level.getBlockState(blockPos);

        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
            return blockState.isFaceSturdy(level, blockPos, Direction.UP);
        else
            return blockState.is(this);
    }

    @Override
    public @Nonnull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(
                HorizontalBlock.FACING,
                rotation.rotate(state.getValue(HorizontalBlock.FACING)));
    }

    @Override
    public @Nonnull BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(HorizontalBlock.FACING))).cycle(DoorBlock.HINGE);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.DOUBLE_BLOCK_HALF, HorizontalBlock.FACING);
    }

    public static float getExplosionPower(ServerWorld level, BlockPos pos, float power){
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
