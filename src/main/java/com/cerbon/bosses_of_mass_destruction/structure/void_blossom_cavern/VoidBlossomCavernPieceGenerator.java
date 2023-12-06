package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.IPieceGenerator;
import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class VoidBlossomCavernPieceGenerator implements IPieceGenerator {

    @Override
    public void generate(WorldGenLevel level, StructureFeatureManager structureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos, IStructurePiece structurePiece) {
        int minY = chunkGenerator.getMinY();
        List<ICaveDecorator> caveDecorators = Arrays.asList(
                new SpikeCaveDecorator(minY, random),
                new MossFloorCaveDecorator(minY, random),
                new MossCeilingCaveDecorator(minY, random),
                new SporeBlossomCaveDecorator(minY, random),
                new BossBlockDecorator(minY)
        );

        generateCave(level, pos.above(17), structurePiece, random, boundingBox, caveDecorators, chunkGenerator.getMinY());

        for (ICaveDecorator caveDecorator : caveDecorators)
            caveDecorator.generate(level, chunkGenerator, random, boundingBox, pos, structurePiece);

    }

    private void generateCave(WorldGenLevel level, BlockPos pos, IStructurePiece structurePiece, Random random, BoundingBox boundingBox, List<ICaveDecorator> caveDecorators, int bottomOfWorld){
        double noiseMultiplier = 0.005;
        UniformInt outerWallDistance = UniformInt.of(3, 4);
        UniformInt pointOffset = UniformInt.of(1, 2);
        int minY = bottomOfWorld - pos.getY();
        int maxY = minY + 32;
        int minXZ = -32;
        int maxXZ = 32;
        double verticalSquish = 2.0;
        int distributionPoints = 5;

        List<Pair<BlockPos, Integer>> randoms = new LinkedList<>();
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(level.getSeed()));
        NormalNoise normalNoise = NormalNoise.create(worldgenRandom, -4, 1.0);
        double d = (double) distributionPoints / (double) outerWallDistance.getMaxValue();
        double airThickness = 1.0 / Math.sqrt(25.2 + d);
        double outerLayerThickness = 1.0 / Math.sqrt(30.2 + d);

        BlockPos randomPos;
        int r = 0;
        while (r < distributionPoints) {
            randomPos = pos.offset(outerWallDistance.sample(random), outerWallDistance.sample(random), outerWallDistance.sample(random));
            randoms.add(
                    Pair.of(
                            randomPos,
                            pointOffset.sample(random)
                    )
            );
            r++;
        }

        Predicate<BlockState> predicate = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);
        Iterator<BlockPos> positions = BlockPos.betweenClosed(pos.offset(minXZ, minY, minXZ), pos.offset(maxXZ, maxY, maxXZ)).iterator();
        while (true) {
            double noisedDistance;
            BlockPos samplePos;
            do {
                if (!positions.hasNext()) {
                    return;
                }

                samplePos = positions.next();

                double noise = normalNoise.getValue(
                        samplePos.getX(),
                        samplePos.getY(),
                        samplePos.getZ()
                ) * noiseMultiplier;

                noisedDistance = 0.0;

                for (Pair<BlockPos, Integer> pair : randoms) {
                    BlockPos distancePos = new BlockPos(samplePos.getX(), (int) (((samplePos.getY() - bottomOfWorld) * verticalSquish) + bottomOfWorld), samplePos.getZ());
                    noisedDistance += Mth.fastInvSqrt(distancePos.distSqr(pair.getFirst()) + (double) (pair.getSecond())) + noise;
                }
            } while (noisedDistance < outerLayerThickness);

            BlockPos finalSamplePos = samplePos;
            Supplier<Boolean> canReplace = () -> predicate.test(level.getBlockState(finalSamplePos)) &&
                    !level.getBlockState(finalSamplePos).isAir() &&
                    level.getBlockState(finalSamplePos).getBlock() != Blocks.BEDROCK &&
                    finalSamplePos.getY() > 4 + bottomOfWorld;

            if (noisedDistance >= airThickness) {
                replaceBlock(
                        canReplace,
                        level,
                        samplePos,
                        structurePiece,
                        boundingBox,
                        Blocks.AIR.defaultBlockState(),
                        caveDecorators
                );
            } else if (noisedDistance >= outerLayerThickness) {
                replaceBlock(
                        canReplace,
                        level,
                        samplePos,
                        structurePiece,
                        boundingBox,
                        samplePos.getY() > 0 ? Blocks.STONE.defaultBlockState() : Blocks.DEEPSLATE.defaultBlockState(),
                        caveDecorators
                );
            }
        }
    }

    private void replaceBlock(Supplier<Boolean> predicate, WorldGenLevel level, BlockPos samplePos, IStructurePiece structurePiece, BoundingBox boundingBox, BlockState blockState, List<ICaveDecorator> caveDecorators){
        if (predicate.get()) structurePiece.placeBlock(level, blockState, samplePos, boundingBox);

        for (ICaveDecorator decorator : caveDecorators)
            decorator.onBlockPlaced(new BlockPos(samplePos), blockState.getBlock());
    }
}
