package com.cerbon.bosses_of_mass_destruction.capability.util;

import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.platform.BMDServices;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class BMDCapabilities {

    public static Optional<ChunkBlockCache> getChunkBlockCache(Level level) {
        return BMDServices.PLATFORM_CAPABILITY_HELPER.getChunkBlockCache(level);
    }

    public static List<Vec3> getPlayerPositions(ServerPlayer player) {
        return BMDServices.PLATFORM_CAPABILITY_HELPER.getPlayerPositions(player);
    }
}
