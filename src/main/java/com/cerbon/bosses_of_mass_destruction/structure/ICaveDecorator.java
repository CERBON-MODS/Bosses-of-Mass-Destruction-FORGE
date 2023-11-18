package com.cerbon.bosses_of_mass_destruction.structure;

import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public interface ICaveDecorator {
    void onBlockPlaced(BlockPos pos, Block block);
    void generate(
            WorldGenLevel level,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox boundingBox,
            BlockPos pos,
            IStructurePiece structurePiece
    );
}
