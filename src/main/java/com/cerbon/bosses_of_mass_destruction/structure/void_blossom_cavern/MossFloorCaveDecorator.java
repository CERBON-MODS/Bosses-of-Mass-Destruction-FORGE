package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MossFloorCaveDecorator implements ICaveDecorator{
    private final int bottomOfWorld;
    private final RandomSource random;

    private final List<BlockPos> mossFloorPositions = new ArrayList<>();

    public MossFloorCaveDecorator(int bottomOfWorld, RandomSource random) {
        this.bottomOfWorld = bottomOfWorld;
        this.random = random;
    }

    @Override
    public void onBlockPlaced(BlockPos pos, Block block) {
        if (pos.getY() == 4 + bottomOfWorld && random.nextInt(80) == 0)
            mossFloorPositions.add(pos);
    }

    @Override
    public void generate(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {
        Map<Pair<Integer, Integer>, List<BlockPos>> groupedMossPositions = mossFloorPositions.stream()
                .collect(Collectors.groupingBy(p -> new Pair<>(p.getX() >> 3, p.getZ() >> 3)));

        List<BlockPos> spacedMossPositions = groupedMossPositions.values().stream()
                .map(list -> list.get(0))
                .toList();

        for (BlockPos mossPos : spacedMossPositions)
            if (boundingBox.isInside(mossPos)){
                ConfiguredFeature<?, ?> configuredFeature = BMDUtils.getConfiguredFeature(level, CaveFeatures.MOSS_PATCH);
                Feature.VEGETATION_PATCH.place(
                        new FeaturePlaceContext<>(
                                Optional.of(configuredFeature),
                                level,
                                chunkGenerator,
                                random,
                                mossPos,
                                (VegetationPatchConfiguration) configuredFeature.config()
                        )
                );
            }
    }
}
