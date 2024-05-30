package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.block.Block;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.util.math.MutableBoundingBox;

import java.util.Random;

public interface ICaveDecorator {
    void onBlockPlaced(BlockPos pos, Block block);
    void generate(
            ISeedReader level,
            ChunkGenerator chunkGenerator,
            Random random,
            MutableBoundingBox boundingBox,
            BlockPos pos,
            IStructurePiece structurePiece
    );
}
