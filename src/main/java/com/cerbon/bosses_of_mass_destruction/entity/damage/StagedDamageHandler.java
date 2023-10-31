package com.cerbon.bosses_of_mass_destruction.entity.damage;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Supplier;

public class StagedDamageHandler implements IDamageHandler {
    private final List<Float> hpPercentRageModes;
    private final Supplier<Void> whenHpBelowThreshold;
    private float previousHpRatio = 1.0f;

    public StagedDamageHandler(List<Float> hpPercentRageModes, Supplier<Void> whenHpBelowThreshold) {
        this.hpPercentRageModes = hpPercentRageModes;
        this.whenHpBelowThreshold = whenHpBelowThreshold;
    }

    @Override
    public void beforeDamage(IEntityStats stats, DamageSource damageSource, float amount) {
        previousHpRatio = hpPercent(stats);
    }

    @Override
    public void afterDamage(IEntityStats stats, DamageSource damageSource, float amount, boolean result) {
        float newHpRatio = hpPercent(stats);
        float firstRageMode = MathUtils.roundedStep(previousHpRatio, hpPercentRageModes, false);
        float secondRageMode = MathUtils.roundedStep(newHpRatio, hpPercentRageModes, false);
        if (firstRageMode != secondRageMode) {
            whenHpBelowThreshold.get();
        }
    }

    @Override
    public boolean shouldDamage(LivingEntity actor, DamageSource damageSource, float amount) {
        return true;
    }

    private float hpPercent(IEntityStats stats) {
        return stats.getHealth() / stats.getMaxHealth();
    }
}

