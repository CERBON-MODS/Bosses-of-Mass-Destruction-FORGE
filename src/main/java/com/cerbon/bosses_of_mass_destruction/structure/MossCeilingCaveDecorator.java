package com.cerbon.bosses_of_mass_destruction.structure;

import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MossCeilingCaveDecorator implements ICaveDecorator{
    private final int bottomOfWorld;
    private final RandomSource random;

    private final List<BlockPos> mossCeilingPositions = new ArrayList<>();

    public MossCeilingCaveDecorator(int bottomOfWorld, RandomSource random) {
        this.bottomOfWorld = bottomOfWorld;
        this.random = random;
    }

    @Override
    public void onBlockPlaced(BlockPos pos, Block block) {
        if (pos.getY() > 18 + bottomOfWorld && random.nextInt(20) == 0 && block != Blocks.AIR)
            mossCeilingPositions.add(pos);
    }

    @Override
    public void generate(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {
        Map<Pair<Integer, Integer>, List<BlockPos>> groupedMossCeilingPositions = mossCeilingPositions.stream()
                .collect(Collectors.groupingBy(p -> new Pair<>(p.getX() >> 3, p.getZ() >> 3)));

        List<BlockPos> spacedMossCeilingPositions = groupedMossCeilingPositions.values().stream()
                .map(list -> list.get(0))
                .toList();

        for (BlockPos mossPoss : spacedMossCeilingPositions)
            if (boundingBox.isInside(mossPoss))
                BMDUtils.getConfiguredFeature(level, CaveFeatures.MOSS_PATCH_CEILING).place(level, chunkGenerator, random, mossPoss);

    }
}
