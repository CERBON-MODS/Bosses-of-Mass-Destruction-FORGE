package com.cerbon.bosses_of_mass_destruction.capability.util;

import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCacheProvider;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class BMDCapabilities {

    public static Optional<ChunkBlockCache> getChunkBlockCache(Level level){
        return level.getCapability(ChunkBlockCacheProvider.CHUNK_BLOCK_CACHE).resolve();
    }
}
