package com.cerbon.bosses_of_mass_destruction.structure.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Random;

public interface IPieceGenerator {
    void generate(
            WorldGenLevel level,
            StructureFeatureManager structureManager,
            ChunkGenerator chunkGenerator,
            Random random,
            BoundingBox box,
            ChunkPos chunkPos,
            BlockPos pos,
            IStructurePiece structurePiece
    );
}
