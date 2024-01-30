package com.cerbon.bosses_of_mass_destruction.platform.services;

import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public interface ICapabilityHelper {
    Optional<ChunkBlockCache> getChunkBlockCache(Level level);
    List<Vec3> getPlayerPositions(ServerPlayer player);
}
