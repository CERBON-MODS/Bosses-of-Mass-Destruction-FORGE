package com.cerbon.bosses_of_mass_destruction.capability;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.block.Block;

import java.util.List;

public interface IChunkBlockCache {
    void addToChunk(ChunkPos chunkPos, Block block, BlockPos pos);
    List<BlockPos> getBlocksFromChunk(ChunkPos chunkPos, Block block);
    void removeFromChunk(ChunkPos chunkPos, Block block, BlockPos pos);
}
