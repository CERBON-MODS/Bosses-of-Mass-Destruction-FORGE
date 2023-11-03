package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.util.CollectionUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Supplier;

public class MinionRageAction implements IActionWithCooldown {
    public static final int numMobs = 9;
    public static final int initialSpawnTimeCooldown = 40;
    public static final int initialBetweenSpawnDelay = 40;
    public static final int spawnDelayDecrease = 3;

    private final LichEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;
    private final MinionAction minionAction;
    private List<Integer> delayTimes = List.of();
    private final int totalMoveTime;

    public MinionRageAction(LichEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel, MinionAction minionAction) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
        this.minionAction = minionAction;

        for (int i = 0; i < numMobs; i++){
            this.delayTimes = CollectionUtils.mapIndexed(CollectionUtils.map(delayTimes, integer -> MathUtils.consecutiveSum(0, integer)),
                    (index, i1) -> initialSpawnTimeCooldown + (index * initialBetweenSpawnDelay) - (i1 * spawnDelayDecrease));
        }

        this.totalMoveTime = !delayTimes.isEmpty() ? delayTimes.get(delayTimes.size() - 1) + MinionAction.minionRuneToMinionSpawnDelay : 0;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayer)) return totalMoveTime;
        performMinionSummon((ServerPlayer) target);
        return totalMoveTime;
    }

    private void performMinionSummon(ServerPlayer target) {
        for (int delayTime : delayTimes) {
            eventScheduler.addEvent(new TimedEvent(() -> minionAction.beginSummonSingleMob(target), delayTime, 1, shouldCancel));
        }
    }
}
