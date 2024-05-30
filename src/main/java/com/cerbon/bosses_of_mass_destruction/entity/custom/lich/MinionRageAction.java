package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MinionRageAction implements IActionWithCooldown {
    private final LichEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;
    private final MinionAction minionAction;
    private final List<Integer> delayTimes = new ArrayList<>();
    private final int totalMoveTime;

    public static final int numMobs = 9;
    public static final int initialSpawnTimeCooldown = 40;
    public static final int initialBetweenSpawnDelay = 40;
    public static final int spawnDelayDecrease = 3;

    public MinionRageAction(LichEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel, MinionAction minionAction) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
        this.minionAction = minionAction;

        for (int i = 0; i < numMobs; i++)
            delayTimes.add(initialSpawnTimeCooldown + (i * initialBetweenSpawnDelay) - (MathUtils.consecutiveSum(0, i) * spawnDelayDecrease));

        this.totalMoveTime = delayTimes.get(delayTimes.size() - 1) + MinionAction.minionRuneToMinionSpawnDelay;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayerEntity)) return totalMoveTime;
        performMinionSummon((ServerPlayerEntity) target);
        return totalMoveTime;
    }

    private void performMinionSummon(ServerPlayerEntity target) {
        for (int delayTime : delayTimes)
            eventScheduler.addEvent(new TimedEvent(() -> minionAction.beginSummonSingleMob(target), delayTime, 1, shouldCancel));
    }
}
