package com.cerbon.bosses_of_mass_destruction.entity.ai;

import com.cerbon.bosses_of_mass_destruction.entity.damage.DamageMemory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class TargetSwitcher {
    private final MobEntity entity;
    private final DamageMemory damageMemory;
    private final int ageToRemember = 20 * 30;

    public TargetSwitcher(MobEntity entity, DamageMemory damageMemory) {
        this.entity = entity;
        this.damageMemory = damageMemory;
    }

    public void trySwitchTarget() {
        Entity newTarget = damageMemory.getDamageHistory().stream()
                .filter(this::filterTargetableEntities)
                .filter(h -> h.ageWhenDamaged + ageToRemember >= entity.tickCount)
                .filter(damageHistory -> damageHistory.source.getEntity() != null)
                .collect(Collectors.groupingBy(h -> h.source.getEntity()))
                .entrySet().stream()
                .max(Comparator.comparingDouble(e -> e.getValue().stream().mapToDouble(DamageMemory.DamageHistory::amount).sum()))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (newTarget != null && !newTarget.equals(entity.getTarget()) && entity.getRandom().nextInt(2) == 0) {
            entity.setTarget((LivingEntity) newTarget);
        }
    }

    private boolean filterTargetableEntities(DamageMemory.DamageHistory damageHistory) {
        Entity attacker = damageHistory.source.getEntity();
        if (attacker instanceof LivingEntity) {
            boolean canSee = entity.getSensing().canSee(attacker);
            double followRange = entity.getAttributeValue(Attributes.FOLLOW_RANGE);
            boolean inRange = entity.distanceToSqr(attacker) < followRange * followRange;
            return canSee && inRange && entity.canAttack((LivingEntity) attacker);
        }

        return false;
    }
}

