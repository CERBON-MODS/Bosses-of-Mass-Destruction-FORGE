package com.cerbon.bosses_of_mass_destruction.forge.platform;

import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.forge.capability.ChunkBlockCacheProvider;
import com.cerbon.bosses_of_mass_destruction.forge.capability.PlayerMoveHistoryProvider;
import com.cerbon.bosses_of_mass_destruction.platform.services.ICapabilityHelper;
import com.cerbon.cerbons_api.api.general.data.HistoricalData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ForgeCapabilityHelper implements ICapabilityHelper {

    @Override
    public Optional<ChunkBlockCache> getChunkBlockCache(Level level) {
        return level.getCapability(ChunkBlockCacheProvider.CHUNK_BLOCK_CACHE).resolve();
    }

    @Override
    public List<Vec3> getPlayerPositions(ServerPlayer player) {
        return player.getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA)
                .map(HistoricalData::getAll)
                .orElse(Collections.emptyList());
    }
}
