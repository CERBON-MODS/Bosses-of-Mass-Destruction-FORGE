package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.HistoricalData;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.WeightedRandom;
import com.cerbon.bosses_of_mass_destruction.entity.ai.TargetSwitcher;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.damage.DamageMemory;
import com.cerbon.bosses_of_mass_destruction.entity.damage.IDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.damage.StagedDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public class ObsidilithMoveLogic implements IActionWithCooldown, IDamageHandler {
    private final Map<Byte, IActionWithCooldown> actions;
    private final ObsidilithEntity entity;

    private final HistoricalData<Byte> moveHistory;
    private boolean shouldDoPillarDefense;
    private final StagedDamageHandler damageHandler;
    private final TargetSwitcher targetSwitcher;

    public ObsidilithMoveLogic(Map<Byte, IActionWithCooldown> actions, ObsidilithEntity entity, DamageMemory damageMemory){
        this.actions = actions;
        this.entity = entity;

        this.moveHistory = new HistoricalData<>((byte) 0, 2);
        this.shouldDoPillarDefense = false;
        this.damageHandler = new StagedDamageHandler(ObsidilithUtils.hpPillarShieldMilestones, () -> shouldDoPillarDefense = true);
        this.targetSwitcher = new TargetSwitcher(entity, damageMemory);
    }

    @Override
    public int perform() {
        targetSwitcher.trySwitchTarget();
        byte moveByte = chooseMove();
        if (actions.get(moveByte) == null)
            throw new IllegalArgumentException(moveByte + " action not registered as an attack");
        IActionWithCooldown action = actions.get(moveByte);
        entity.level().broadcastEntityEvent(entity, moveByte);
        return action.perform();
    }

    private Byte chooseMove(){
        LivingEntity target = entity.getTarget();
        if (target == null) return ObsidilithUtils.burstAttackStatus;

        byte nextMove;
        if (shouldDoPillarDefense){
            shouldDoPillarDefense = false;
            nextMove = ObsidilithUtils.pillarDefenseStatus;
        }else {
            WeightedRandom<Byte> random = new WeightedRandom<>();
            double distanceToTarget = target.distanceToSqr(entity);
            double burstWeight = distanceToTarget < 36 ? 1 : 0.0;
            double anvilWeight = distanceToTarget < 36 || moveHistory.getAll().contains(ObsidilithUtils.anvilAttackStatus) ? 0.0 : 1.0;
            double waveWeight = distanceToTarget < 36 ? 0.5 : 1.0;
            double spikeWeight = distanceToTarget < 36 ? 0.0 : 1.0;

            random.add(burstWeight, ObsidilithUtils.burstAttackStatus);
            random.add(anvilWeight, ObsidilithUtils.anvilAttackStatus);
            random.add(spikeWeight, ObsidilithUtils.spikeAttackStatus);
            random.add(waveWeight, ObsidilithUtils.waveAttackStatus);

            nextMove = random.next();
        }
        moveHistory.set(nextMove);
        return nextMove;
    }

    @Override
    public void beforeDamage(IEntityStats stats, DamageSource damageSource, float amount) {
        damageHandler.beforeDamage(stats, damageSource, amount);
    }

    @Override
    public void afterDamage(IEntityStats stats, DamageSource damageSource, float amount, boolean result) {
        damageHandler.afterDamage(stats, damageSource, amount, result);
    }

    @Override
    public boolean shouldDamage(LivingEntity actor, DamageSource damageSource, float amount) {
        return true;
    }
}
