package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;
import java.util.Arrays;

public class EffectsImmunity implements IMobEffectFilter {
    private final List<MobEffect> mobEffectList;

    public EffectsImmunity(MobEffect... statusEffects) {
        this.mobEffectList = Arrays.asList(statusEffects);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return !mobEffectList.contains(effect.getEffect());
    }
}

