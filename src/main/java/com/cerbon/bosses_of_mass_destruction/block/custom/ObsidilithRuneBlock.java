package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Random;

public class ObsidilithRuneBlock extends Block {

    public ObsidilithRuneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(@Nonnull BlockState state, @Nonnull ServerWorld level, @Nonnull BlockPos pos, @Nonnull Random random) {
        linkToEntities(level, pos);
    }

    private void linkToEntities(ServerWorld level, BlockPos pos){
        level.getEntitiesOfClass(ObsidilithEntity.class, new AxisAlignedBB(pos).inflate(15.0, 40.0, 15.0)).forEach(
                entity -> entity.addActivePillar(pos)
        );
    }

    @Override
    public void onPlace(@Nonnull BlockState state, World level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean movedByPiston) {
        level.getBlockTicks().scheduleTick(pos, this, 10);
    }
}
