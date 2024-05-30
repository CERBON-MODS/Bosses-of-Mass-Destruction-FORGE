package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import java.util.List;
import java.util.Arrays;

public class EffectsImmunity implements IMobEffectFilter {
    private final List<Effect> mobEffectList;

    public EffectsImmunity(Effect... statusEffects) {
        this.mobEffectList = Arrays.asList(statusEffects);
    }

    @Override
    public boolean canBeAffected(EffectInstance effect) {
        return !mobEffectList.contains(effect.getEffect());
    }
}

