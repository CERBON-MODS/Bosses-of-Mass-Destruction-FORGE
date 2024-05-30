package com.cerbon.bosses_of_mass_destruction.structure.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.util.math.MutableBoundingBox;

import java.util.Random;

public interface IPieceGenerator {
    void generate(
            ISeedReader level,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            Random random,
            MutableBoundingBox box,
            ChunkPos chunkPos,
            BlockPos pos,
            IStructurePiece structurePiece
    );
}
