package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEntity;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ObsidilithSummonBlock extends Block {
    public static final BooleanProperty eye = BlockStateProperties.EYE;
    protected final VoxelShape frameShape = box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
    protected final VoxelShape eyeShape = box(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
    protected final VoxelShape frameWithEyeShape = VoxelShapes.or(frameShape, eyeShape);

    public ObsidilithSummonBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(eye, false));
    }

    @Override
    public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public @Nonnull VoxelShape getShape(BlockState state, @Nonnull IBlockReader level, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return state.getValue(eye) ? frameWithEyeShape : frameShape;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        return defaultBlockState().setValue(eye, false);
    }

    @Override
    public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @Nonnull World level, @Nonnull BlockPos pos) {
        return state.getValue(eye) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(eye);
    }

    public static void onEnderEyeUsed(ItemUseContext context, CallbackInfoReturnable<ActionResultType> cir){
        World level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (blockState.is(BMDBlocks.OBSIDILITH_SUMMON_BLOCK.get()) && !blockState.getValue(EndPortalFrameBlock.HAS_EYE)){
            EventScheduler eventScheduler = BMDCapabilities.getLevelEventScheduler(level);

            if (level.isClientSide){
                cir.setReturnValue(ActionResultType.SUCCESS);
                addSummonEntityEffects(eventScheduler, blockPos);
            } else {
                BlockState blockState2 = blockState.setValue(EndPortalFrameBlock.HAS_EYE, true);
                pushEntitiesUp(blockState, blockState2, level, blockPos);
                level.setBlock(blockPos, blockState2, 2);
                context.getItemInHand().shrink(1);
                level.levelEvent(1503, blockPos, 0);

                addSummonEntityEvent(eventScheduler, level, blockPos);
                cir.setReturnValue(ActionResultType.PASS);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void addSummonEntityEffects(EventScheduler eventScheduler, BlockPos blockPos){
        Vector3d centralPos = VecUtils.asVec3(blockPos.above()).add(VecUtils.unit.scale(0.5));
        Vector3d particleVel = VecUtils.yAxis.scale(-0.03);
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

    private static void addSummonEntityEvent(EventScheduler eventScheduler, World level, BlockPos blockPos){
        Vector3d pos = VecUtils.asVec3(blockPos).add(new Vector3d(0.5, 0.0, 0.5));
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                            ObsidilithEntity obsidilithEntity = BMDEntities.OBSIDILITH.get().create(level);
                            if (obsidilithEntity != null){
                                obsidilithEntity.setPacketCoordinates(pos.x, pos.y, pos.z);
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
