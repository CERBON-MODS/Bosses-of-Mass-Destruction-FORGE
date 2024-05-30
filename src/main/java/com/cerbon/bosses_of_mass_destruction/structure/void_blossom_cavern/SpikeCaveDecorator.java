package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.structure.util.IStructurePiece;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;
import java.util.stream.Collectors;

public class SpikeCaveDecorator implements ICaveDecorator{
    private final int bottomOfWorld;
    private final Random random;

    private final List<BlockPos> spikePositions = new ArrayList<>();
    private final List<Vector3d> baseBlocks = MathUtils.buildBlockCircle(4.2);

    public SpikeCaveDecorator(int bottomOfWorld, Random random) {
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
    public void generate(ISeedReader level, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, BlockPos pos, IStructurePiece structurePiece) {
        Map<Pair<Integer, Integer>, List<BlockPos>> groupedSpikePositions = spikePositions.stream()
                .collect(Collectors.groupingBy(p -> new Pair<>(p.getX() >> 2, p.getZ() >> 2)));

        List<BlockPos> spacesSpikePositions = groupedSpikePositions.values().stream()
                .map(list -> list.get(0))
                .collect(Collectors.toList());

        for (BlockPos outerPos : spacesSpikePositions){
            Vector3d centerDirection = VecUtils.planeProject(VecUtils.asVec3(pos.subtract(outerPos)), VecUtils.yAxis).normalize();
            Vector3d tip = centerDirection
                    .scale(5 + random.nextInt(3))
                    .add(VecUtils.yAxis.scale((7 + random.nextInt(5))));
            generateSpike(outerPos, outerPos.offset(new BlockPos(tip)), structurePiece, level, boundingBox, centerDirection);
        }
    }

    private void generateSpike(BlockPos origin, BlockPos tip, IStructurePiece structurePiece, ISeedReader level, MutableBoundingBox boundingBox, Vector3d centerDirection){
        Vector3d centerDirectionPos = centerDirection.scale(3.0);
        Set<BlockPos> blockSet = baseBlocks.stream().map(BlockPos::new).collect(Collectors.toSet());
        Set<BlockPos> innerBlockSet = blockSet.stream().filter(pos -> pos.distSqr(centerDirectionPos, true) < Math.pow(2.0, 2)).collect(Collectors.toSet());
        Set<BlockPos> middleBlockSet = blockSet.stream().filter(block -> !innerBlockSet.contains(block)).filter(block -> block.distSqr(centerDirectionPos, true) < Math.pow(3.7, 2)).collect(Collectors.toSet());
        Set<BlockPos> outerBlockSet = blockSet.stream().filter(pos -> !middleBlockSet.contains(pos) && !innerBlockSet.contains(pos)).collect(Collectors.toSet());

        for (BlockPos blockPos : innerBlockSet){
            List<BlockPos> blocksInLine = MathUtils.getBlocksInLine(blockPos.offset(origin), tip);
            for (BlockPos pos : blocksInLine){
                BlockState blockState = Blocks.BEDROCK.defaultBlockState();
                structurePiece.placeBlock(level, blockState, pos, boundingBox);
            }
        }

        for (BlockPos blockPos : middleBlockSet){
            List<BlockPos> blocksInLine = MathUtils.getBlocksInLine(blockPos.offset(origin), tip);
            for (BlockPos pos : blocksInLine)
                structurePiece.placeBlock(level, Blocks.BEDROCK.defaultBlockState(), pos, boundingBox);
        }

        for (BlockPos blockPos : outerBlockSet){
            List<BlockPos> blocksInLine = MathUtils.getBlocksInLine(blockPos.offset(origin), tip);
            for (BlockPos pos : blocksInLine)
                structurePiece.placeBlock(level, Blocks.BEDROCK.defaultBlockState(), pos, boundingBox);
        }
    }
}
