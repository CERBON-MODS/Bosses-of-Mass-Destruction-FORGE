package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;

public class ChunkCacheBlockEntity extends TileEntity {
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

    public static void tick(World level, BlockPos pos, BlockState state, ChunkCacheBlockEntity entity){
        if (!entity.added){
            BMDCapabilities.getChunkBlockCache(level).ifPresent(chunkBlockCache ->
                    chunkBlockCache.addToChunk(new ChunkPos(pos), entity.block, pos)
            );

            entity.added = true;
        }
    }
}
