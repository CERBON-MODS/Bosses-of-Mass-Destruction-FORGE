package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class VoidBlossomSpikeTick implements IEntityTick<ServerWorld> {
    private final VoidBlossomEntity entity;

    public VoidBlossomSpikeTick(VoidBlossomEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick(ServerWorld level) {
        AxisAlignedBB spikeHitbox = new AxisAlignedBB(entity.position(), entity.position()).inflate(3.0, 3.0, 3.0).move(0.0, 1.5, 0.0);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, spikeHitbox, livingEntity -> livingEntity != entity);

        for (LivingEntity target : targets){
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if (target.position().distanceToSqr(entity.position()) < Math.pow(3.0, 2))
                target.hurt(DamageSource.thorns(entity), damage);
        }
    }
}
