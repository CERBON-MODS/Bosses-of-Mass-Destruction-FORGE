package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.IPieceGenerator;
import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;

import java.util.Random;

public class VoidBlossomCavernPieceGenerator implements IPieceGenerator {
    @Override
    public void generate(ISeedReader level, StructureManager structureManager, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox box, ChunkPos chunkPos, BlockPos pos, IStructurePiece structurePiece) {

    }
//
//    @Override
//    public void generate(ISeedReader level, StructureManager structureManager, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos, IStructurePiece structurePiece) {
//        int minY = chunkGenerator.getMinY();
//        List<ICaveDecorator> caveDecorators = Arrays.asList(
//                new SpikeCaveDecorator(minY, random),
//                new MossFloorCaveDecorator(minY, random),
//                new MossCeilingCaveDecorator(minY, random),
//                new SporeBlossomCaveDecorator(minY, random),
//                new BossBlockDecorator(minY)
//        );
//
//        generateCave(level, pos.above(17), structurePiece, random, boundingBox, caveDecorators, chunkGenerator.getMinY());
//
//        for (ICaveDecorator caveDecorator : caveDecorators)
//            caveDecorator.generate(level, chunkGenerator, random, boundingBox, pos, structurePiece);
//
//    }
//
//    private void generateCave(ISeedReader level, BlockPos pos, IStructurePiece structurePiece, Random random, MutableBoundingBox boundingBox, List<ICaveDecorator> caveDecorators, int bottomOfWorld){
//        double noiseMultiplier = 0.005;
//        UniformInt outerWallDistance = UniformInt.of(3, 4);
//        UniformInt pointOffset = UniformInt.of(1, 2);
//        int minY = bottomOfWorld - pos.getY();
//        int maxY = minY + 32;
//        int minXZ = -32;
//        int maxXZ = 32;
//        double verticalSquish = 2.0;
//        int distributionPoints = 5;
//
//        List<Pair<BlockPos, Integer>> randoms = new LinkedList<>();
//        SharedSeedRandom worldgenRandom = new SharedSeedRandom(new LegacyRandomSource(level.getSeed()));
//        MaxMinNoiseMixer normalNoise = MaxMinNoiseMixer.create(worldgenRandom, -4, 1.0);
//        double d = (double) distributionPoints / (double) outerWallDistance.getMaxValue();
//        double airThickness = 1.0 / Math.sqrt(25.2 + d);
//        double outerLayerThickness = 1.0 / Math.sqrt(30.2 + d);
//
//        BlockPos randomPos;
//        int r = 0;
//        while (r < distributionPoints) {
//            randomPos = pos.offset(outerWallDistance.sample(random), outerWallDistance.sample(random), outerWallDistance.sample(random));
//            randoms.add(
//                    Pair.of(
//                            randomPos,
//                            pointOffset.sample(random)
//                    )
//            );
//            r++;
//        }
//
//        Predicate<BlockState> predicate = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);
//        Iterator<BlockPos> positions = BlockPos.betweenClosed(pos.offset(minXZ, minY, minXZ), pos.offset(maxXZ, maxY, maxXZ)).iterator();
//        while (true) {
//            double noisedDistance;
//            BlockPos samplePos;
//            do {
//                if (!positions.hasNext()) {
//                    return;
//                }
//
//                samplePos = positions.next();
//
//                double noise = normalNoise.getValue(
//                        samplePos.getX(),
//                        samplePos.getY(),
//                        samplePos.getZ()
//                ) * noiseMultiplier;
//
//                noisedDistance = 0.0;
//
//                for (Pair<BlockPos, Integer> pair : randoms) {
//                    BlockPos distancePos = new BlockPos(samplePos.getX(), (int) (((samplePos.getY() - bottomOfWorld) * verticalSquish) + bottomOfWorld), samplePos.getZ());
//                    noisedDistance += MathHelper.fastInvSqrt(distancePos.distSqr(pair.getFirst()) + (double) (pair.getSecond())) + noise;
//                }
//            } while (noisedDistance < outerLayerThickness);
//
//            BlockPos finalSamplePos = samplePos;
//            Supplier<Boolean> canReplace = () -> predicate.test(level.getBlockState(finalSamplePos)) &&
//                    !level.getBlockState(finalSamplePos).isAir() &&
//                    level.getBlockState(finalSamplePos).getBlock() != Blocks.BEDROCK &&
//                    finalSamplePos.getY() > 4 + bottomOfWorld;
//
//            if (noisedDistance >= airThickness) {
//                replaceBlock(
//                        canReplace,
//                        level,
//                        samplePos,
//                        structurePiece,
//                        boundingBox,
//                        Blocks.AIR.defaultBlockState(),
//                        caveDecorators
//                );
//            } else if (noisedDistance >= outerLayerThickness) {
//                replaceBlock(
//                        canReplace,
//                        level,
//                        samplePos,
//                        structurePiece,
//                        boundingBox,
//                        samplePos.getY() > 0 ? Blocks.STONE.defaultBlockState() : Blocks.DEEPSLATE.defaultBlockState(),
//                        caveDecorators
//                );
//            }
//        }
//    }
//
//    private void replaceBlock(Supplier<Boolean> predicate, ISeedReader level, BlockPos samplePos, IStructurePiece structurePiece, MutableBoundingBox boundingBox, BlockState blockState, List<ICaveDecorator> caveDecorators){
//        if (predicate.get()) structurePiece.placeBlock(level, blockState, samplePos, boundingBox);
//
//        for (ICaveDecorator decorator : caveDecorators)
//            decorator.onBlockPlaced(new BlockPos(samplePos), blockState.getBlock());
//    }
}
