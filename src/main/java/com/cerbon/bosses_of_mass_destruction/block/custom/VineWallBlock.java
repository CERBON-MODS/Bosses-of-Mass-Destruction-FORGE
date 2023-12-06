package com.cerbon.bosses_of_mass_destruction.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VineWallBlock extends IronBarsBlock {

    public VineWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull Random random) {
        level.destroyBlock(pos, false);
    }
}
