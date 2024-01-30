package com.cerbon.bosses_of_mass_destruction.entity.damage;

import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface IDamageHandler {
    void beforeDamage(IEntityStats stats, DamageSource damageSource, float amount);
    void afterDamage(IEntityStats stats, DamageSource damageSource, float amount, boolean result);
    boolean shouldDamage(LivingEntity actor, DamageSource damageSource, float amount);

}
