package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.particle.ParticleFactories;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChiseledStoneAltarBlock extends Block {
    private final ClientParticleBuilder paleSparkleParticleFactory = new ClientParticleBuilder(BMDParticles.DOWNSPARKLE.get())
            .color(f -> MathUtils.lerpVec(f, BMDColors.WHITE, BMDColors.GREY))
            .age(20, 30)
            .colorVariation(0.1)
            .scale(f -> 0.15f - (f * 0.1f));

    public ClientParticleBuilder blueFireParticleFactory = ParticleFactories.soulFlame()
            .color(LichUtils.blueColorFade)
            .age(30, 40)
            .colorVariation(0.5)
            .scale(f -> 0.15f - (f * 0.1f));

    public ChiseledStoneAltarBlock(Properties properties) {
        super(properties);
        this.stateDefinition.any().setValue(BlockStateProperties.LIT, false);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.stateDefinition.any().setValue(BlockStateProperties.LIT, false);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return state.getValue(BlockStateProperties.LIT) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.LIT);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(BlockStateProperties.LIT)){
            if (random.nextInt(3) == 0){
                blueFireParticleFactory.build(
                        VecUtils.asVec3(pos).add(0.5, 1.0, 0.5).add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).multiply(0.5, 0.5, 0.5)),
                        VecUtils.yAxis.multiply(0.05, 0.05, 0.05)
                );
            }
        }else {
            paleSparkleParticleFactory.build(
                    VecUtils.asVec3(pos).add(0.5, 2.0, 0.5).add(RandomUtils.randVec().multiply(0.5, 0.5, 0.5)),
                    VecUtils.yAxis.multiply(-0.05, -0.05, -0.05)
            );
        }
    }
}
