package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Random;

public interface ICaveDecorator {
    void onBlockPlaced(BlockPos pos, Block block);
    void generate(
            WorldGenLevel level,
            ChunkGenerator chunkGenerator,
            Random random,
            BoundingBox boundingBox,
            BlockPos pos,
            IStructurePiece structurePiece
    );
}
