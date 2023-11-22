package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class VoidBlossomSpikeTick implements IEntityTick<ServerLevel> {
    private final VoidBlossomEntity entity;

    public VoidBlossomSpikeTick(VoidBlossomEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick(ServerLevel level) {
        AABB spikeHitbox = new AABB(entity.position(), entity.position()).inflate(3.0, 3.0, 3.0).move(0.0, 1.5, 0.0);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, spikeHitbox, livingEntity -> livingEntity != entity);

        for (LivingEntity target : targets){
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if (target.position().distanceToSqr(entity.position()) < Math.pow(3.0, 2))
                target.hurt(entity.level().damageSources().thorns(entity), damage);
        }
    }
}
