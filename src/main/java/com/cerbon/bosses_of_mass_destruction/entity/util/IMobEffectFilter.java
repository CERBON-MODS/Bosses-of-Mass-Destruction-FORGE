package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.potion.EffectInstance;

public interface IMobEffectFilter {
    boolean canBeAffected(EffectInstance effect);
}
