package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

public class MobWardBlock extends BaseEntityBlock {
    public static final VoxelShape blockShape = box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    public static final VoxelShape thinBlockShape = box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    public static final EnumProperty<TripleBlockPart> tripleBlockPart = EnumProperty.create("triple_part", TripleBlockPart.class);

    public MobWardBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MobWardBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BMDBlockEntities.MOB_WARD.get(), ChunkCacheBlockEntity::tick);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(new TranslatableComponent("item.bosses_of_mass_destruction.mob_ward.tooltip").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction direction, @NotNull BlockState newState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos posFrom) {
        TripleBlockPart thisState = state.getValue(tripleBlockPart);
        BlockState superState = super.updateShape(state, direction, newState, level, pos, posFrom);
        BlockState air = Blocks.AIR.defaultBlockState();
        boolean otherState = newState.is(this);
        BlockState facingState;
        facingState = otherState
                ? state.setValue(HorizontalDirectionalBlock.FACING, newState.getValue(HorizontalDirectionalBlock.FACING))
                : air;

        return switch (thisState) {
            case BOTTOM -> switch (direction) {
                case UP -> (otherState && newState.getValue(tripleBlockPart) == TripleBlockPart.MIDDLE) ? facingState : air;
                default -> superState;
            };
            case MIDDLE -> switch (direction) {
                case UP -> (otherState && newState.getValue(tripleBlockPart) == TripleBlockPart.TOP) ? facingState : air;
                case DOWN -> (otherState && newState.getValue(tripleBlockPart) == TripleBlockPart.BOTTOM) ? facingState : air;
                default -> superState;
            };
            case TOP -> switch (direction) {
                case DOWN -> (otherState && newState.getValue(tripleBlockPart) == TripleBlockPart.MIDDLE) ? facingState : air;
                default -> superState;
            };
        };
    }

    @Override
    public void playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
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

    private void checkBreakPart(BlockPos pos, Level level, BlockState state, Player player, TripleBlockPart part){
        BlockState blockState = level.getBlockState(pos);

        if (blockState.getBlock() == state.getBlock() && blockState.getValue(tripleBlockPart) == part){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(player, 2001, pos, getId(blockState));
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();

        if (blockPos.getY() < ctx.getLevel().getMaxBuildHeight() - 2 && ctx.getLevel().getBlockState(blockPos.above()).canBeReplaced(ctx) && ctx.getLevel().getBlockState(blockPos.above(2)).canBeReplaced(ctx))
            return getStateDefinition().any().setValue(DoorBlock.FACING, ctx.getHorizontalDirection()).setValue(tripleBlockPart, TripleBlockPart.BOTTOM);
        else
            return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(tripleBlockPart, TripleBlockPart.MIDDLE), 3);
        level.setBlock(pos.above(2), state.setValue(tripleBlockPart, TripleBlockPart.TOP), 3);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockPos = pos.below();
        BlockState blockState = level.getBlockState(blockPos);

        if (state.getValue(tripleBlockPart) == TripleBlockPart.BOTTOM)
            return blockState.isFaceSturdy(level, blockPos, Direction.UP);
        else
            return blockState.is(this);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HorizontalDirectionalBlock.FACING, rotation.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(DoorBlock.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, tripleBlockPart);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(tripleBlockPart) == TripleBlockPart.TOP ? blockShape : thinBlockShape;
    }

    @Override
    public void animateTick(BlockState state, @NotNull Level lvel, @NotNull BlockPos pos, @NotNull Random random) {
        if (state.getValue(tripleBlockPart) == TripleBlockPart.TOP){
            if (random.nextInt(3) == 0){
                Vec3 vecPos = VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5));
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
                                ), Vec3.ZERO
                );
            }
        }
    }

    private Vec3 calcParticlePos(Vec3 vecPos, double randomOffset, double randomRadius, double randomHeight, double age){
        return vecPos.add(new Vec3(
                Math.sin(age + randomOffset) * randomRadius,
                randomHeight + age * 0.3,
                Math.cos(age + randomOffset) * randomRadius
        ));
    }

    public static void canSpawn(ServerLevel serverLevel, BlockPos.MutableBlockPos pos, CallbackInfoReturnable<Boolean> cir){
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
