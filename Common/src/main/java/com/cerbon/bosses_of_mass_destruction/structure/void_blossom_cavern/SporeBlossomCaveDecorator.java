package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SporeBlossomCaveDecorator implements ICaveDecorator{
    private final int bottomOfWorld;
    private final RandomSource random;

    private final List<BlockPos> sporeBlossomPositions = new ArrayList<>();
    private final ConfiguredFeature<SimpleBlockConfiguration, Feature<SimpleBlockConfiguration>> sporeBlossom = new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SPORE_BLOSSOM)));
    private final PlacedFeature placedFeature = new PlacedFeature(
            Holder.direct(sporeBlossom), Arrays.asList(
                    CountPlacement.of(25),
                    InSquarePlacement.spread(),
                    PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                    EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12),
                    RandomOffsetPlacement.vertical(ConstantInt.of(-1))
            )
    );

    public SporeBlossomCaveDecorator(int bottomOfWorld, RandomSource random) {
        this.bottomOfWorld = bottomOfWorld;
        this.random = random;
    }

    @Override
    public void onBlockPlaced(BlockPos pos, Block block) {
        if (pos.getY() > 20 + bottomOfWorld && random.nextInt(20) == 0 && block != Blocks.AIR)
            sporeBlossomPositions.add(pos);
    }

    @Override
    public void generate(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {
        Map<Pair<Integer, Integer>, List<BlockPos>> groupedSporeBlossomPositions = sporeBlossomPositions.stream()
                .collect(Collectors.groupingBy(p -> new Pair<>(p.getX() >> 3, p.getZ() >> 3)));

        List<BlockPos> spacedSporeBlossomPositions = groupedSporeBlossomPositions.values().stream()
                .map(list -> list.get(0))
                .toList();

        for (BlockPos sporePos : spacedSporeBlossomPositions)
            if (boundingBox.isInside(sporePos))
                placedFeature.place(level, chunkGenerator, random, sporePos);
    }
}
