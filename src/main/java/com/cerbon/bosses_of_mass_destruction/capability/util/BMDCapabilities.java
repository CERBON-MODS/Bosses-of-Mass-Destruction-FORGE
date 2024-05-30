package com.cerbon.bosses_of_mass_destruction.capability.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.HistoricalData;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCacheProvider;
import com.cerbon.bosses_of_mass_destruction.capability.LevelEventSchedulerProvider;
import com.cerbon.bosses_of_mass_destruction.capability.PlayerMoveHistoryProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BMDCapabilities {
    public static List<Vector3d> getPlayerPositions(ServerPlayerEntity player){
        return player.getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA)
                .map(HistoricalData::getAll)
                .orElse(Collections.emptyList());
    }

    public static Optional<ChunkBlockCache> getChunkBlockCache(World level){
        return level.getCapability(ChunkBlockCacheProvider.CHUNK_BLOCK_CACHE).resolve();
    }

    public static EventScheduler getLevelEventScheduler(World level){
        return level.getCapability(LevelEventSchedulerProvider.EVENT_SCHEDULER).resolve().orElse(null);
    }
}
