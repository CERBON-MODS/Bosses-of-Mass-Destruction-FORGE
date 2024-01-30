package com.cerbon.bosses_of_mass_destruction.neoforge.attachment.saved_data;

import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class LevelChunkBlockCache extends SavedData {
    private static final Factory<LevelChunkBlockCache> FACTORY = new Factory<>(LevelChunkBlockCache::new, LevelChunkBlockCache::new);
    public static LevelChunkBlockCache INSTANCE;
    private static final String DATA_KEY = "bomd_level_chunk_block_cache";
    private ChunkBlockCache chunkBlockCache;

    public LevelChunkBlockCache() {
        this(new CompoundTag());
    }

    public LevelChunkBlockCache(CompoundTag tag) {}

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        return tag;
    }

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
