package com.cerbon.bosses_of_mass_destruction.projectile.util;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.EntityRayTraceResult;

import java.util.List;
import java.util.function.Predicate;

public class ExemptEntities implements Predicate<EntityRayTraceResult> {
    final List<EntityType<?>> exemptEntities;

    public ExemptEntities(List<EntityType<?>> exemptEntities){
        this.exemptEntities = exemptEntities;
    }

    @Override
    public boolean test(EntityRayTraceResult t) {
        return !exemptEntities.contains(t.getEntity().getType());
    }
}
