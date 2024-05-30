package com.cerbon.bosses_of_mass_destruction.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Random;

public class VineWallBlock extends PaneBlock {

    public VineWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(@Nonnull BlockState state, ServerWorld level, @Nonnull BlockPos pos, @Nonnull Random random) {
        level.destroyBlock(pos, false);
    }
}
