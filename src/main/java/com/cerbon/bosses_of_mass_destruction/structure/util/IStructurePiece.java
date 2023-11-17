package com.cerbon.bosses_of_mass_destruction.structure.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public interface IStructurePiece {
    void placeBlock(WorldGenLevel level, BlockState block, BlockPos pos, BoundingBox box);
}
