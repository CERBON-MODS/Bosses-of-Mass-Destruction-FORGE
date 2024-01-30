package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEntity;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.static_utilities.CapabilityUtils;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ObsidilithSummonBlock extends Block {
    public static final BooleanProperty eye = BlockStateProperties.EYE;
    protected final VoxelShape frameShape = box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
    protected final VoxelShape eyeShape = box(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
    protected final VoxelShape frameWithEyeShape = Shapes.or(frameShape, eyeShape);

    public ObsidilithSummonBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(eye, false));
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(eye) ? frameWithEyeShape : frameShape;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return defaultBlockState().setValue(eye, false);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return state.getValue(eye) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(eye);
    }

    public static void onEnderEyeUsed(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir){
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (blockState.is(BMDBlocks.OBSIDILITH_SUMMON_BLOCK.get()) && !blockState.getValue(EndPortalFrameBlock.HAS_EYE)){
            EventScheduler eventScheduler = CapabilityUtils.getLevelEventScheduler(level);

            if (level.isClientSide){
                cir.setReturnValue(InteractionResult.SUCCESS);
                addSummonEntityEffects(eventScheduler, blockPos);
            } else {
                BlockState blockState2 = blockState.setValue(EndPortalFrameBlock.HAS_EYE, true);
                pushEntitiesUp(blockState, blockState2, level, blockPos);
                level.setBlock(blockPos, blockState2, 2);
                context.getItemInHand().shrink(1);
                level.levelEvent(1503, blockPos, 0);

                addSummonEntityEvent(eventScheduler, level, blockPos);
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private static void addSummonEntityEffects(EventScheduler eventScheduler, BlockPos blockPos){
        Vec3 centralPos = VecUtils.asVec3(blockPos.above()).add(VecUtils.unit.scale(0.5));
        Vec3 particleVel = VecUtils.yAxis.scale(-0.03);
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> Particles.activateParticleFactory.build(
                                centralPos.add(RandomUtils.randVec().scale(2.0)),
                                particleVel
                        ),
                        0,
                        80,
                        () -> false
                )
        );
    }

    private static void addSummonEntityEvent(EventScheduler eventScheduler, Level level, BlockPos blockPos){
        Vec3 pos = VecUtils.asVec3(blockPos).add(new Vec3(0.5, 0.0, 0.5));
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                            ObsidilithEntity obsidilithEntity = BMDEntities.OBSIDILITH.get().create(level);
                            if (obsidilithEntity != null){
                                obsidilithEntity.syncPacketPositionCodec(pos.x, pos.y, pos.z);
                                obsidilithEntity.absMoveTo(pos.x, pos.y, pos.z);
                                level.addFreshEntity(obsidilithEntity);
                            }
                        },
                        100
                )
        );
    }

    public static class Particles {
        public static final ClientParticleBuilder activateParticleFactory = new ClientParticleBuilder(BMDParticles.PILLAR_RUNE.get())
                .scale(f -> (float) (Math.sin((double) f * Math.PI)) * 0.05f)
                .age(30);
    }
}
