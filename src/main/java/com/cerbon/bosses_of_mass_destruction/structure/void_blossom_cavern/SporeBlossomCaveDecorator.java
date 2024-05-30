package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.Random;

public class SporeBlossomCaveDecorator implements ICaveDecorator{
    @Override
    public void onBlockPlaced(BlockPos pos, Block block) {

    }

    @Override
    public void generate(ISeedReader level, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {

    }
//    private final int bottomOfWorld;
//    private final Random random;
//
//    private final List<BlockPos> sporeBlossomPositions = new ArrayList<>();
//    private final ConfiguredFeature<BlockWithContextConfig, Feature<BlockWithContextConfig>> sporeBlossom = new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new BlockWithContextConfig(BlockStateProvider.simple(Blocks.SPORE_BLOSSOM)));
//    private final PlacedFeature placedFeature = new PlacedFeature(
//            Holder.direct(sporeBlossom), Arrays.asList(
//                    CountPlacement.of(25),
//                    InSquarePlacement.spread(),
//                    PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
//                    EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12),
//                    RandomOffsetPlacement.vertical(ConstantInt.of(-1))
//            )
//    );
//
//    public SporeBlossomCaveDecorator(int bottomOfWorld, Random random) {
//        this.bottomOfWorld = bottomOfWorld;
//        this.random = random;
//    }
//
//    @Override
//    public void onBlockPlaced(BlockPos pos, Block block) {
//        if (pos.getY() > 20 + bottomOfWorld && random.nextInt(20) == 0 && block != Blocks.AIR)
//            sporeBlossomPositions.add(pos);
//    }
//
//    @Override
//    public void generate(ISeedReader level, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {
//        Map<Pair<Integer, Integer>, List<BlockPos>> groupedSporeBlossomPositions = sporeBlossomPositions.stream()
//                .collect(Collectors.groupingBy(p -> new Pair<>(p.getX() >> 3, p.getZ() >> 3)));
//
//        List<BlockPos> spacedSporeBlossomPositions = groupedSporeBlossomPositions.values().stream()
//                .map(list -> list.get(0))
//                .collect(Collectors.toList());();
//
//        for (BlockPos sporePos : spacedSporeBlossomPositions)
//            if (boundingBox.isInside(sporePos))
//                placedFeature.place(level, chunkGenerator, random, sporePos);
//    }
}
