package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ParticleFactories;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChiseledStoneAltarBlock extends Block {
    public static final BooleanProperty lit = BlockStateProperties.LIT;

    public ChiseledStoneAltarBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(lit, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.stateDefinition.any().setValue(lit, false);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return state.getValue(lit) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(lit);
    }

    @Override
    public void animateTick(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.getValue(lit)){
            if (random.nextInt(3) == 0){
                Particles.blueFireParticleFactory.build(
                        VecUtils.asVec3(pos).add(0.5, 1.0, 0.5).add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).multiply(0.5, 0.5, 0.5)),
                        VecUtils.yAxis.multiply(0.05, 0.05, 0.05)
                );
            }
        } else
            Particles.paleSparkleParticleFactory.build(
                    VecUtils.asVec3(pos).add(0.5, 2.0, 0.5).add(RandomUtils.randVec().multiply(0.5, 0.5, 0.5)),
                    VecUtils.yAxis.multiply(-0.05, -0.05, -0.05)
            );
    }

    public static class Particles {
        public static final ClientParticleBuilder paleSparkleParticleFactory = new ClientParticleBuilder(BMDParticles.DOWNSPARKLE.get())
                .color(f -> MathUtils.lerpVec(f, Vec3Colors.WHITE, Vec3Colors.GREY))
                .age(20, 30)
                .colorVariation(0.1)
                .scale(f -> 0.15f - (f * 0.1f));

        public static final ClientParticleBuilder blueFireParticleFactory = ParticleFactories.soulFlame()
                .color(LichUtils.blueColorFade)
                .age(30, 40)
                .colorVariation(0.5)
                .scale(f -> 0.15f - (f * 0.1f));
    }
}
