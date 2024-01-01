package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.cerbon.cerbons_api.api.static_utilities.MobUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class EntityAdapter implements IEntity {
    private final LivingEntity entity;

    public EntityAdapter(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public Vec3 getDeltaMovement() {
        return entity.getDeltaMovement();
    }

    @Override
    public Vec3 getPos() {
        return entity.position();
    }

    @Override
    public Vec3 getEyePos() {
        return MobUtils.eyePos(entity);
    }

    @Override
    public Vec3 getLookAngle() {
        return entity.getLookAngle();
    }

    @Override
    public int getTickCount() {
        return entity.tickCount;
    }

    @Override
    public boolean isAlive() {
        return entity.isAlive();
    }

    @Override
    public IEntity target() {
        if(entity instanceof Mob) {
            LivingEntity target = ((Mob) entity).getTarget();
            if(target != null)
                return new EntityAdapter(target);
        }
        return null;
    }
}

