package com.cerbon.bosses_of_mass_destruction.damagesource;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

public class UnshieldableDamageSource extends EntityDamageSource {

    public UnshieldableDamageSource(Entity entity) {
        super("mob", entity);
    }
}
