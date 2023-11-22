package com.cerbon.bosses_of_mass_destruction.projectile.util;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;
import java.util.function.Predicate;

public class ExemptEntities implements Predicate<EntityHitResult> {
    final List<EntityType<?>> exemptEntities;

    public ExemptEntities(List<EntityType<?>> exemptEntities){
        this.exemptEntities = exemptEntities;
    }

    @Override
    public boolean test(EntityHitResult t) {
        return !exemptEntities.contains(t.getEntity().getType());
    }
}
