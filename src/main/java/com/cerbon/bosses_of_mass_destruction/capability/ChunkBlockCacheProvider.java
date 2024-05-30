package com.cerbon.bosses_of_mass_destruction.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChunkBlockCacheProvider implements ICapabilityProvider {
    @CapabilityInject(ChunkBlockCache.class)
    public static final Capability<ChunkBlockCache> CHUNK_BLOCK_CACHE = null;

    private ChunkBlockCache chunkBlockCache;
    private final LazyOptional<ChunkBlockCache> optional = LazyOptional.of(this::createChunkCache);

    private ChunkBlockCache createChunkCache() {
        if(this.chunkBlockCache == null)
            this.chunkBlockCache = new ChunkBlockCache();

        return this.chunkBlockCache;
    }

    @Override
    public @Nonnull <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CHUNK_BLOCK_CACHE)
            return optional.cast();

        return LazyOptional.empty();
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(ChunkBlockCache.class, new Capability.IStorage<ChunkBlockCache>() {

            @Nullable
            @Override
            public INBT writeNBT(Capability<ChunkBlockCache> capability, ChunkBlockCache instance, Direction side) {return null;}

            @Override
            public void readNBT(Capability<ChunkBlockCache> capability, ChunkBlockCache instance, Direction side, INBT nbt) {}

        }, ChunkBlockCache::new);
    }
}
