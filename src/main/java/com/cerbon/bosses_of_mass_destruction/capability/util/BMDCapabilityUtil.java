package com.cerbon.bosses_of_mass_destruction.capability.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.HistoricalData;
import com.cerbon.bosses_of_mass_destruction.capability.PlayerMoveHistoryProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BMDCapabilityUtil {
    public static List<Vec3> getPlayerPositions(ServerPlayer player){
        return player.getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA)
                .map(HistoricalData::getAll)
                .orElse(Collections.emptyList());
    }

}
