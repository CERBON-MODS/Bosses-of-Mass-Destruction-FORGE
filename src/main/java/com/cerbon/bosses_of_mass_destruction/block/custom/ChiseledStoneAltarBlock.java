package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.particle.ParticleFactories;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class ChiseledStoneAltarBlock extends Block {
    public static final BooleanProperty lit = BlockStateProperties.LIT;

    public ChiseledStoneAltarBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(lit, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        return this.stateDefinition.any().setValue(lit, false);
    }

    @Override
    public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @Nonnull World level, @Nonnull BlockPos pos) {
        return state.getValue(lit) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(lit);
    }

    @Override
    public void animateTick(BlockState state, @Nonnull World level, @Nonnull BlockPos pos, @Nonnull Random random) {
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
                .color(f -> MathUtils.lerpVec(f, BMDColors.WHITE, BMDColors.GREY))
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
