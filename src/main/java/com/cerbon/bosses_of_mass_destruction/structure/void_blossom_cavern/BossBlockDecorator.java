package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BossBlockDecorator implements ICaveDecorator {
    private final int bottomOfWorld;

    public BossBlockDecorator(int bottomOfWorld) {
        this.bottomOfWorld = bottomOfWorld;
    }

    @Override
    public void onBlockPlaced(BlockPos pos, Block block) {}

    @Override
    public void generate(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {
        structurePiece.placeBlock(level, BMDBlocks.VOID_BLOSSOM_SUMMON_BLOCK.get().defaultBlockState(), bossBlockOffset(pos, bottomOfWorld), boundingBox);
    }

    public static BlockPos bossBlockOffset(BlockPos pos, int bottomOfWorld){
        return new BlockPos(pos.getX() + 3, bottomOfWorld + 5, pos.getZ() + 3);
    }
}
