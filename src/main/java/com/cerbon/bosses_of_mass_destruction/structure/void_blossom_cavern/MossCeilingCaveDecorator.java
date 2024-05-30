package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.Random;

public class MossCeilingCaveDecorator implements ICaveDecorator{
    @Override
    public void onBlockPlaced(BlockPos pos, Block block) {

    }

    @Override
    public void generate(ISeedReader level, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {

    }
//    private final int bottomOfWorld;
//    private final Random random;
//
//    private final List<BlockPos> mossCeilingPositions = new ArrayList<>();
//
//    public MossCeilingCaveDecorator(int bottomOfWorld, Random random) {
//        this.bottomOfWorld = bottomOfWorld;
//        this.random = random;
//    }
//
//    @Override
//    public void onBlockPlaced(BlockPos pos, Block block) {
//        if (pos.getY() > 18 + bottomOfWorld && random.nextInt(20) == 0 && block != Blocks.AIR)
//            mossCeilingPositions.add(pos);
//    }
//
//    @Override
//    public void generate(ISeedReader level, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {
//        Map<Pair<Integer, Integer>, List<BlockPos>> groupedMossCeilingPositions = mossCeilingPositions.stream()
//                .collect(Collectors.groupingBy(p -> new Pair<>(p.getX() >> 3, p.getZ() >> 3)));
//
//        List<BlockPos> spacedMossCeilingPositions = groupedMossCeilingPositions.values().stream()
//                .map(list -> list.get(0))
//                .toList();
//
//        for (BlockPos mossPoss : spacedMossCeilingPositions)
//            if (boundingBox.isInside(mossPoss))
//                CaveFeatures.MOSS_PATCH_CEILING.value().place(level, chunkGenerator, random, mossPoss);
//
//    }
}
