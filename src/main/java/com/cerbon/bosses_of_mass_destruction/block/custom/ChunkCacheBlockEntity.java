package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import net.minecraft.block.Block;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.ChunkPos;

public class ChunkCacheBlockEntity extends TileEntity implements ITickableTileEntity {
    private final Block block;
    private boolean added = false;

    public ChunkCacheBlockEntity(Block block, TileEntityType<?> type) {
        super(type);
        this.block = block;
    }

    @Override
    public void setRemoved() {
        if (level != null){
            BMDCapabilities.getChunkBlockCache(level).ifPresent(chunkBlockCache ->
                    chunkBlockCache.removeFromChunk(new ChunkPos(worldPosition), block, worldPosition)
            );

            added = false;
        }
        super.setRemoved();
    }

    @Override
    public void tick() {
        if (level == null) return;

        if (!this.added){
            BMDCapabilities.getChunkBlockCache(level).ifPresent(chunkBlockCache ->
                    chunkBlockCache.addToChunk(new ChunkPos(getBlockPos()), this.block, getBlockPos())
            );

            this.added = true;
        }
    }
}
