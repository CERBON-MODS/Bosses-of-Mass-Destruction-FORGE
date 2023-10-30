package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.world.effect.MobEffectInstance;

public interface IMobEffectFilter {
    boolean canHaveMobEffect(MobEffectInstance effect);
}
