package com.cerbon.bosses_of_mass_destruction.capability.util;

import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCacheProvider;
import com.cerbon.bosses_of_mass_destruction.capability.PlayerMoveHistoryProvider;
import com.cerbon.cerbons_api.api.general.data.HistoricalData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BMDCapabilities {
    public static List<Vec3> getPlayerPositions(ServerPlayer player){
        return player.getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA)
                .map(HistoricalData::getAll)
                .orElse(Collections.emptyList());
    }

    public static Optional<ChunkBlockCache> getChunkBlockCache(Level level){
        return level.getCapability(ChunkBlockCacheProvider.CHUNK_BLOCK_CACHE).resolve();
    }
}
