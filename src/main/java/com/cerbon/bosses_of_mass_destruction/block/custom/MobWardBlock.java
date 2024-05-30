package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class MobWardBlock extends ContainerBlock {
    public static final VoxelShape blockShape = box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    public static final VoxelShape thinBlockShape = box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    public static final EnumProperty<TripleBlockPart> tripleBlockPart = EnumProperty.create("triple_part", TripleBlockPart.class);

    public MobWardBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(HorizontalBlock.FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader blockReader) {
        return new MobWardBlockEntity();
    }

    @Override
    public @Nonnull BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.MODEL;
    }

//    @Nullable
//    @Override
//    public <T extends TileEntity> BlockEntityTicker<T> getTicker(@Nonnull World level, @Nonnull BlockState state, @Nonnull TileEntityType<T> blockEntityType) {
//        return createTickerHelper(blockEntityType, BMDBlockEntities.MOB_WARD.get(), ChunkCacheBlockEntity::tick);
//    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable IBlockReader level, List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent("item.bosses_of_mass_destruction.mob_ward.tooltip").withStyle(TextFormatting.DARK_GRAY));
    }

    @Override
    public @Nonnull BlockState updateShape(BlockState state, @Nonnull Direction direction, @Nonnull BlockState newState, @Nonnull IWorld level, @Nonnull BlockPos pos, @Nonnull BlockPos posFrom) {
        TripleBlockPart thisState = state.getValue(tripleBlockPart);
        BlockState superState = super.updateShape(state, direction, newState, level, pos, posFrom);
        BlockState air = Blocks.AIR.defaultBlockState();
        boolean otherState = newState.is(this);
        BlockState facingState = otherState
                ? state.setValue(HorizontalBlock.FACING, newState.getValue(HorizontalBlock.FACING))
                : air;

        switch (thisState) {
            case BOTTOM:
                if (direction == Direction.UP)
                    return (otherState && newState.getValue(tripleBlockPart) == TripleBlockPart.MIDDLE) ? facingState : air;
                return superState;
            case MIDDLE:
                switch (direction) {
                    case UP:
                        return (otherState && newState.getValue(tripleBlockPart) == TripleBlockPart.TOP) ? facingState : air;
                    case DOWN:
                        return (otherState && newState.getValue(tripleBlockPart) == TripleBlockPart.BOTTOM) ? facingState : air;
                    default:
                        return superState;
                }
            case TOP:
                if (direction == Direction.DOWN)
                    return (otherState && newState.getValue(tripleBlockPart) == TripleBlockPart.MIDDLE) ? facingState : air;
                return superState;
            default:
                return superState;
        }
    }

    @Override
    public void playerWillDestroy(World level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity player) {
        if (!level.isClientSide && player.isCreative()){
            TripleBlockPart part = state.getValue(tripleBlockPart);

            if (part == TripleBlockPart.MIDDLE){
                checkBreakPart(pos.below(), level, state, player, TripleBlockPart.BOTTOM);
                checkBreakPart(pos.above(), level, state, player, TripleBlockPart.TOP);

            }else if (part == TripleBlockPart.TOP){
                checkBreakPart(pos.below(2), level, state, player, TripleBlockPart.BOTTOM);
                checkBreakPart(pos.below(), level, state, player, TripleBlockPart.MIDDLE);
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    private void checkBreakPart(BlockPos pos, World level, BlockState state, PlayerEntity player, TripleBlockPart part){
        BlockState blockState = level.getBlockState(pos);

        if (blockState.getBlock() == state.getBlock() && blockState.getValue(tripleBlockPart) == part){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(player, 2001, pos, getId(blockState));
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();

        if (blockPos.getY() < ctx.getLevel().getMaxBuildHeight() - 2 && ctx.getLevel().getBlockState(blockPos.above()).canBeReplaced(ctx) && ctx.getLevel().getBlockState(blockPos.above(2)).canBeReplaced(ctx))
            return getStateDefinition().any().setValue(DoorBlock.FACING, ctx.getHorizontalDirection()).setValue(tripleBlockPart, TripleBlockPart.BOTTOM);
        else
            return null;
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(tripleBlockPart, TripleBlockPart.MIDDLE), 3);
        level.setBlock(pos.above(2), state.setValue(tripleBlockPart, TripleBlockPart.TOP), 3);
    }

    @Override
    public boolean canSurvive(@Nonnull BlockState state, IWorldReader level, BlockPos pos) {
        BlockPos blockPos = pos.below();
        BlockState blockState = level.getBlockState(blockPos);

        if (state.getValue(tripleBlockPart) == TripleBlockPart.BOTTOM)
            return blockState.isFaceSturdy(level, blockPos, Direction.UP);
        else
            return blockState.is(this);
    }

    @Override
    public @Nonnull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HorizontalBlock.FACING, rotation.rotate(state.getValue(HorizontalBlock.FACING)));
    }

    @Override
    public @Nonnull BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(DoorBlock.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HorizontalBlock.FACING, tripleBlockPart);
    }

    @Override
    public @Nonnull VoxelShape getShape(BlockState state, @Nonnull IBlockReader level, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return state.getValue(tripleBlockPart) == TripleBlockPart.TOP ? blockShape : thinBlockShape;
    }

    @Override
    public void animateTick(BlockState state, @Nonnull World lvel, @Nonnull BlockPos pos, @Nonnull Random random) {
        if (state.getValue(tripleBlockPart) == TripleBlockPart.TOP){
            if (random.nextInt(3) == 0){
                Vector3d vecPos = VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5));
                double randomHeight = RandomUtils.randomDouble(0.25) + 0.25;
                double randomRadius = RandomUtils.randomDouble(0.1) + 0.3;
                double randomOffset = RandomUtils.randomDouble(Math.PI);
                Particles.blueFireParticleFactory.continuousPosition(simpleParticle ->
                        calcParticlePos(
                                vecPos,
                                randomOffset,
                                randomRadius,
                                randomHeight,
                                simpleParticle.getAge() * 0.1
                        )).build(
                                calcParticlePos(
                                        vecPos,
                                        randomOffset,
                                        randomRadius,
                                        randomHeight,
                                        .0
                                ), Vector3d.ZERO
                );
            }
        }
    }

    private Vector3d calcParticlePos(Vector3d vecPos, double randomOffset, double randomRadius, double randomHeight, double age){
        return vecPos.add(new Vector3d(
                Math.sin(age + randomOffset) * randomRadius,
                randomHeight + age * 0.3,
                Math.cos(age + randomOffset) * randomRadius
        ));
    }

    public static void canSpawn(ServerWorld serverLevel, BlockPos.Mutable pos, CallbackInfoReturnable<Boolean> cir){
        if (!cir.getReturnValue()) return;

        ChunkPos chunkPos = new ChunkPos(pos);
        BMDCapabilities.getChunkBlockCache(serverLevel).ifPresent(capability -> {
            for (int x = chunkPos.x - 4; x <= chunkPos.x + 4; x++)
                for (int z = chunkPos.z - 4; z <= chunkPos.z + 4; z++){
                    List<BlockPos> blocks = capability.getBlocksFromChunk(new ChunkPos(x, z), BMDBlocks.MOB_WARD.get());
                    if (blocks == null) return;
                    for (BlockPos blockPos : blocks) {
                        if (Math.abs(blockPos.getX() - pos.getX()) < 64 && Math.abs(blockPos.getY() - pos.getY()) < 64 && Math.abs(blockPos.getZ() - pos.getZ()) < 64) {
                            cir.setReturnValue(false);
                            break;
                        }
                    }
                }
        });
    }

    private static class Particles {
        private static final ClientParticleBuilder blueFireParticleFactory = new ClientParticleBuilder(BMDParticles.SOUL_FLAME.get())
                .color(LichUtils.blueColorFade)
                .age(30, 40)
                .colorVariation(0.5)
                .scale(f -> 0.15f - (f * 0.1f));
    }
}
