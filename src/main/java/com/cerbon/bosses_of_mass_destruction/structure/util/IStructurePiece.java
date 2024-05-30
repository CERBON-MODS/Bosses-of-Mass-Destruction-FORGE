package com.cerbon.bosses_of_mass_destruction.structure.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MutableBoundingBox;

public interface IStructurePiece {
    void placeBlock(ISeedReader level, BlockState block, BlockPos pos, MutableBoundingBox box);
}
