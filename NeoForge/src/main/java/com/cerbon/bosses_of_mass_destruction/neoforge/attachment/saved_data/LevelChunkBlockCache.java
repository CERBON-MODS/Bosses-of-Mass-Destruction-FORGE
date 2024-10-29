package com.cerbon.bosses_of_mass_destruction.neoforge.attachment.saved_data;

import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public class LevelChunkBlockCache extends SavedData {
    private static final Factory<LevelChunkBlockCache> FACTORY = new Factory<>(LevelChunkBlockCache::new, LevelChunkBlockCache::new);
    public static LevelChunkBlockCache INSTANCE;
    private static final String DATA_KEY = "bomd_level_chunk_block_cache";
    private ChunkBlockCache chunkBlockCache;

    public LevelChunkBlockCache() {
        this(new CompoundTag(), new HolderLookup.Provider() {

            @Override
            public @NotNull Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
                return Stream.empty();
            }

            @Override
            public <T> @NotNull Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey) {
                return Optional.empty();
            }
        });
    }

    public LevelChunkBlockCache(CompoundTag tag, HolderLookup.@NotNull Provider registries) {}

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {return tag;}

    public static ChunkBlockCache get(Level level) {
        LevelChunkBlockCache levelChunkBlockCache = getSavedData(level);

        if (levelChunkBlockCache.chunkBlockCache == null)
            levelChunkBlockCache.chunkBlockCache = new ChunkBlockCache();

        return levelChunkBlockCache.chunkBlockCache;
    }

    private static LevelChunkBlockCache getSavedData(Level level) {
        return level.isClientSide ? INSTANCE : ((ServerLevel) level).getDataStorage().computeIfAbsent(FACTORY, DATA_KEY);
    }
}
