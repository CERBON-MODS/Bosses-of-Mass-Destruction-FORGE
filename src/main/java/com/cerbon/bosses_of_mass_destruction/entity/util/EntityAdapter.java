package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.vector.Vector3d;

public class EntityAdapter implements IEntity {
    private final LivingEntity entity;

    public EntityAdapter(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public Vector3d getDeltaMovement() {
        return entity.getDeltaMovement();
    }

    @Override
    public Vector3d getPos() {
        return entity.position();
    }

    @Override
    public Vector3d getEyePos() {
        return MobUtils.eyePos(entity);
    }

    @Override
    public Vector3d getLookAngle() {
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
        if(entity instanceof MobEntity) {
            LivingEntity target = ((MobEntity) entity).getTarget();
            if(target != null)
                return new EntityAdapter(target);
        }
        return null;
    }
}

