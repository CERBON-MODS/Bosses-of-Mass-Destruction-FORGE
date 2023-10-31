package com.cerbon.bosses_of_mass_destruction.entity.damage;

import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class DamagedAttackerNotSeen implements IDamageHandler {
    private final LichEntity actor;
    private final Consumer<LivingEntity> callback;

    public DamagedAttackerNotSeen(LichEntity actor, Consumer<LivingEntity> callback) {
        this.actor = actor;
        this.callback = callback;
    }

    @Override
    public void beforeDamage(IEntityStats stats, DamageSource damageSource, float amount) {}

    @Override
    public void afterDamage(IEntityStats stats, DamageSource damageSource, float amount, boolean result) {
        if (actor.getTarget() == null) {
            Entity attacker = damageSource.getEntity();
            if (attacker instanceof LivingEntity) {
                callback.accept((LivingEntity) attacker);
            }
        }
    }

    @Override
    public boolean shouldDamage(LivingEntity actor, DamageSource damageSource, float amount) {
        return true;
    }
}
