package com.cerbon.bosses_of_mass_destruction.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class ChunkBlockCache implements IChunkBlockCache {
    private final HashMap<ChunkPos, HashMap<Block, HashSet<BlockPos>>> map = new HashMap<>();

    private final Map<Block, Integer> blockCounts = new HashMap<>();

    public boolean hasBlocks(Block block) {
        // A subclass may provide additional positions from another source.
        return this.getClass() != ChunkBlockCache.class || this.blockCounts.getOrDefault(block, 0) != 0;
    }

    @Override
    public void addToChunk(ChunkPos chunkPos, Block block, BlockPos pos) {
        HashMap<Block, HashSet<BlockPos>> chunk = map.getOrDefault(chunkPos, new HashMap<>());
        HashSet<BlockPos> blocks = chunk.getOrDefault(block, new HashSet<>());
        if (blocks.add(pos)) {
            this.blockCounts.merge(block, 1, Integer::sum);
        }
        chunk.put(block, blocks);
        map.put(chunkPos, chunk);
    }

    @Override
    public List<BlockPos> getBlocksFromChunk(ChunkPos chunkPos, Block block) {
        List<BlockPos> positions = new ArrayList<>();

        if (map.containsKey(chunkPos) && map.get(chunkPos).containsKey(block))
             positions = map.get(chunkPos).get(block).stream().toList();

        return positions;
    }

    @Override
    public void removeFromChunk(ChunkPos chunkPos, Block block, BlockPos pos) {
        if (map.containsKey(chunkPos) && map.get(chunkPos).containsKey(block)
                && map.get(chunkPos).get(block).remove(pos)) {
            this.blockCounts.computeIfPresent(block, (key, count) -> count == 1 ? null : count - 1);
        }
    }
}
