package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SpikeCaveDecorator implements ICaveDecorator{
    private final int bottomOfWorld;
    private final RandomSource random;

    private final List<BlockPos> spikePositions = new ArrayList<>();
    private final List<Vec3> baseBlocks = MathUtils.buildBlockCircle(4.2);

    public SpikeCaveDecorator(int bottomOfWorld, RandomSource random) {
        this.bottomOfWorld = bottomOfWorld;
        this.random = random;
    }

    @Override
    public void onBlockPlaced(BlockPos pos, Block block) {
        double spikeSpacing = Math.pow(((double) random.nextInt(20) + 10), 2);
        if (pos.getY() == 5 + bottomOfWorld && block != Blocks.AIR && spikePositions.stream().allMatch(it -> it.distSqr(pos) > spikeSpacing))
            spikePositions.add(pos);
    }

    @Override
    public void generate(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {
        Map<Pair<Integer, Integer>, List<BlockPos>> groupedSpikePositions = spikePositions.stream()
                .collect(Collectors.groupingBy(p -> new Pair<>(p.getX() >> 2, p.getZ() >> 2)));

        List<BlockPos> spacesSpikePositions = groupedSpikePositions.values().stream()
                .map(list -> list.get(0))
                .toList();

        for (BlockPos outerPos : spacesSpikePositions){
            Vec3 centerDirection = VecUtils.planeProject(VecUtils.asVec3(pos.subtract(outerPos)), VecUtils.yAxis).normalize();
            Vec3 tip = centerDirection
                    .scale(5 + random.nextInt(3))
                    .add(VecUtils.yAxis.scale((7 + random.nextInt(5))));
            generateSpike(outerPos, outerPos.offset(BlockPos.containing(tip)), structurePiece, level, boundingBox, centerDirection);
        }
    }

    private void generateSpike(BlockPos origin, BlockPos tip, IStructurePiece structurePiece, WorldGenLevel level, BoundingBox boundingBox, Vec3 centerDirection){
        Vec3 centerDirectionPos = centerDirection.scale(3.0);
        Set<BlockPos> blockSet = baseBlocks.stream().map(BlockPos::containing).collect(Collectors.toSet());
        Set<BlockPos> innerBlockSet = blockSet.stream().filter(pos -> pos.distToCenterSqr(centerDirectionPos) < Math.pow(2.0, 2)).collect(Collectors.toSet());
        Set<BlockPos> middleBlockSet = blockSet.stream().filter(block -> !innerBlockSet.contains(block)).filter(block -> block.distToCenterSqr(centerDirectionPos) < Math.pow(3.7, 2)).collect(Collectors.toSet());
        Set<BlockPos> outerBlockSet = blockSet.stream().filter(pos -> !middleBlockSet.contains(pos) && !innerBlockSet.contains(pos)).collect(Collectors.toSet());

        for (BlockPos blockPos : innerBlockSet){
            List<BlockPos> blocksInLine = MathUtils.getBlocksInLine(blockPos.offset(origin), tip);
            for (BlockPos pos : blocksInLine){
                BlockState blockState = random.nextInt(16) == 0 ? Blocks.BUDDING_AMETHYST.defaultBlockState() : Blocks.AMETHYST_BLOCK.defaultBlockState();
                structurePiece.placeBlock(level, blockState, pos, boundingBox);
            }
        }

        for (BlockPos blockPos : middleBlockSet){
            List<BlockPos> blocksInLine = MathUtils.getBlocksInLine(blockPos.offset(origin), tip);
            for (BlockPos pos : blocksInLine)
                structurePiece.placeBlock(level, Blocks.CALCITE.defaultBlockState(), pos, boundingBox);
        }

        for (BlockPos blockPos : outerBlockSet){
            List<BlockPos> blocksInLine = MathUtils.getBlocksInLine(blockPos.offset(origin), tip);
            for (BlockPos pos : blocksInLine)
                structurePiece.placeBlock(level, Blocks.SMOOTH_BASALT.defaultBlockState(), pos, boundingBox);
        }
    }
}
