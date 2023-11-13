package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.WeightedRandom;
import com.cerbon.bosses_of_mass_destruction.entity.ai.TargetSwitcher;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.function.Supplier;

public class VoidBlossomMoveLogic implements IActionWithCooldown {
    private final Map<Byte, IActionWithCooldown> actions;
    private final VoidBlossomEntity entity;
    private final Supplier<Boolean> doBlossom;
    private final TargetSwitcher targetSwitcher;

    public VoidBlossomMoveLogic(Map<Byte, IActionWithCooldown> actions, VoidBlossomEntity entity, Supplier<Boolean> doBlossom, TargetSwitcher targetSwitcher) {
        this.actions = actions;
        this.entity = entity;
        this.doBlossom = doBlossom;
        this.targetSwitcher = targetSwitcher;
    }

    @Override
    public int perform() {
        targetSwitcher.trySwitchTarget();
        LivingEntity target = entity.getTarget();
        if (target == null) return 20;
        float healthPercentage = entity.getHealth() / entity.getMaxHealth();
        WeightedRandom<Byte> random = new WeightedRandom<>();
        double shortDistanceRate = target.distanceTo(entity) > 21 ? 0.0 : 1.0;
        double spikeWeight = 1.0;
        double sporeWeight = healthPercentage < VoidBlossomEntity.hpMilestones.get(3) ? shortDistanceRate : 0.0;
        double bladeWeight = healthPercentage < VoidBlossomEntity.hpMilestones.get(2) ? 1.0 : 0.0;

        byte moveByte;
        if (doBlossom.get())
            moveByte = VoidBlossomAttacks.blossomAction;
        else {
            random.add(spikeWeight, VoidBlossomAttacks.spikeAttack);
            random.add(shortDistanceRate, VoidBlossomAttacks.spikeWaveAttack);
            random.add(sporeWeight, VoidBlossomAttacks.sporeAttack);
            random.add(bladeWeight, VoidBlossomAttacks.bladeAttack);

            moveByte = random.next();
        }

        IActionWithCooldown action = actions.get(moveByte);
        if (action == null) throw new IllegalArgumentException(moveByte + " action not registered as an attack");
        entity.level().broadcastEntityEvent(entity, moveByte);
        return action.perform();
    }
}
