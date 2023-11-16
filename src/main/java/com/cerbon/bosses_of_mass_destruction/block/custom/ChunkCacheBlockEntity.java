package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChunkCacheBlockEntity extends BlockEntity {
    private final Block block;
    private boolean added = false;

    public ChunkCacheBlockEntity(Block block, BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.block = block;
    }

    @Override
    public void setRemoved() {
        if (level != null){
            BMDCapabilities.getChunkBlockCache(level).ifPresent(chunkBlockCache ->
                    chunkBlockCache.removeFromChunk(new ChunkPos(worldPosition), block, worldPosition));

            added = false;
        }
        super.setRemoved();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ChunkCacheBlockEntity entity){
        if (!entity.added){
            BMDCapabilities.getChunkBlockCache(level).ifPresent(chunkBlockCache ->
                    chunkBlockCache.addToChunk(new ChunkPos(pos), entity.block, pos));

            entity.added = true;
        }
    }
}
