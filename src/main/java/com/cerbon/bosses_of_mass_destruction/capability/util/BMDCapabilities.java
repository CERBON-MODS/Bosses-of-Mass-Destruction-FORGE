package com.cerbon.bosses_of_mass_destruction.capability.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class BMDCapabilities {
    private static EventScheduler eventScheduler;
    private static ChunkBlockCache chunkBlockCache;

//    public static List<Vec3> getPlayerPositions(ServerPlayer player){
//        return player.getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA)
//                .map(HistoricalData::getAll)
//                .orElse(Collections.emptyList());
//    }

    public static Optional<ChunkBlockCache> getChunkBlockCache(Level level){
        if (chunkBlockCache == null)
            chunkBlockCache = new ChunkBlockCache();

        return Optional.of(chunkBlockCache);
    }

    public static EventScheduler getLevelEventScheduler(Level level){
        if (eventScheduler == null)
            eventScheduler = new EventScheduler();

        return eventScheduler;
    }
}
