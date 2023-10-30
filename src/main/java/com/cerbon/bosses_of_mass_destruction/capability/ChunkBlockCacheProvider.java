package com.cerbon.bosses_of_mass_destruction.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCapability
public class ChunkBlockCacheProvider implements ICapabilityProvider {
    public static Capability<ChunkBlockCache> CHUNK_BLOCK_CACHE = CapabilityManager.get(new CapabilityToken<>() {});

    private ChunkBlockCache chunkBlockCache = null;
    private final LazyOptional<ChunkBlockCache> optional = LazyOptional.of(this::createChunkCache);

    private ChunkBlockCache createChunkCache() {
        if(this.chunkBlockCache == null)
            this.chunkBlockCache = new ChunkBlockCache();

        return this.chunkBlockCache;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CHUNK_BLOCK_CACHE)
            return optional.cast();

        return LazyOptional.empty();
    }
}