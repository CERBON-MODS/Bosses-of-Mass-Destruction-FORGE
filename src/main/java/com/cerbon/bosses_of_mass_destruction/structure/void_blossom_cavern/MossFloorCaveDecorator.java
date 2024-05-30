package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MossFloorCaveDecorator implements ICaveDecorator{
    private final int bottomOfWorld;
    private final Random random;

    private final List<BlockPos> mossFloorPositions = new ArrayList<>();

    public MossFloorCaveDecorator(int bottomOfWorld, Random random) {
        this.bottomOfWorld = bottomOfWorld;
        this.random = random;
    }

    @Override
    public void onBlockPlaced(BlockPos pos, Block block) {
        if (pos.getY() == 4 + bottomOfWorld && random.nextInt(80) == 0)
            mossFloorPositions.add(pos);
    }

    @Override
    public void generate(ISeedReader level, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {
//        Map<Pair<Integer, Integer>, List<BlockPos>> groupedMossPositions = mossFloorPositions.stream()
//                .collect(Collectors.groupingBy(p -> new Pair<>(p.getX() >> 3, p.getZ() >> 3)));
//
//        List<BlockPos> spacedMossPositions = groupedMossPositions.values().stream()
//                .map(list -> list.get(0))
//                .toList();
//
//        for (BlockPos mossPos : spacedMossPositions)
//            if (boundingBox.isInside(mossPos)){
//                ConfiguredFeature<?, ?> configuredFeature = Feature.FLOWER.();
//                Feature.FLOWER.place(
//                        new FlowersFeature<>(
//                                Optional.of(configuredFeature),
//                                level,
//                                chunkGenerator,
//                                random,
//                                mossPos,
//                                (VegetationPatchConfiguration) configuredFeature.config()
//                        )
//                );
//            }
    }
}
