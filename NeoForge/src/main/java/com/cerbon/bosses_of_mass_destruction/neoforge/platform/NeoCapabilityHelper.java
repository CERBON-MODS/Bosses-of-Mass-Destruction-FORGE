package com.cerbon.bosses_of_mass_destruction.neoforge.platform;

import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.neoforge.attachment.BMDAttachments;
import com.cerbon.bosses_of_mass_destruction.neoforge.attachment.saved_data.LevelChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.platform.services.ICapabilityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class NeoCapabilityHelper implements ICapabilityHelper {

    @Override
    public Optional<ChunkBlockCache> getChunkBlockCache(Level level) {
        return Optional.of(LevelChunkBlockCache.get(level));
    }

    @Override
    public List<Vec3> getPlayerPositions(ServerPlayer player) {
        return BMDAttachments.getPlayerPositions(player);
    }
}
