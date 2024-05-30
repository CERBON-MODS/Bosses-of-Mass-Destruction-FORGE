package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.entity.damage.IDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Supplier;

public class ShieldDamageHandler implements IDamageHandler {
    private final Supplier<Boolean> isShielded;

    public ShieldDamageHandler(Supplier<Boolean> isShielded){
        this.isShielded = isShielded;
    }

    @Override
    public void beforeDamage(IEntityStats stats, DamageSource damageSource, float amount) {}

    @Override
    public void afterDamage(IEntityStats stats, DamageSource damageSource, float amount, boolean result) {}

    @Override
    public boolean shouldDamage(LivingEntity actor, DamageSource damageSource, float amount) {
        if (isShielded.get() && !damageSource.isBypassInvul()){
            if (!damageSource.isProjectile()){
                Entity entity = damageSource.getEntity();

                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.knockback(0.5f, actor.getX() - livingEntity.getX(), actor.getZ() - livingEntity.getZ());
                }
            }
            actor.playSound(BMDSounds.ENERGY_SHIELD.get(), 1.0f, BMDUtils.randomPitch(actor.getRandom()));
            return false;
        }
        return true;
    }
}
