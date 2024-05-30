package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.block.Block;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.util.math.MutableBoundingBox;

import java.util.Random;

public class BossBlockDecorator implements ICaveDecorator {
    private final int bottomOfWorld;

    public BossBlockDecorator(int bottomOfWorld) {
        this.bottomOfWorld = bottomOfWorld;
    }

    @Override
    public void onBlockPlaced(BlockPos pos, Block block) {}

    @Override
    public void generate(ISeedReader level, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {
        structurePiece.placeBlock(level, BMDBlocks.VOID_BLOSSOM_SUMMON_BLOCK.get().defaultBlockState(), bossBlockOffset(pos, bottomOfWorld), boundingBox);
    }

    public static BlockPos bossBlockOffset(BlockPos pos, int bottomOfWorld){
        return new BlockPos(pos.getX() + 3, bottomOfWorld + 5, pos.getZ() + 3);
    }
}
