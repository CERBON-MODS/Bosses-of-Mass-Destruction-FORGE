package com.cerbon.bosses_of_mass_destruction.damagesource;

import net.minecraft.util.EntityDamageSource;
import net.minecraft.entity.Entity;

public class UnshieldableDamageSource extends EntityDamageSource {

    public UnshieldableDamageSource(Entity entity) {
        super("mob", entity);
    }
}
