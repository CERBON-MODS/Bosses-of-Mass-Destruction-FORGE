package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.entity.LivingEntity;

public class EntityStats implements IEntityStats{
    final LivingEntity livingEntity;

    public EntityStats(LivingEntity livingEntity){
        this.livingEntity = livingEntity;
    }

    @Override
    public float getMaxHealth() {
        return livingEntity.getMaxHealth();
    }

    @Override
    public float getHealth() {
        return livingEntity.getHealth();
    }
}
