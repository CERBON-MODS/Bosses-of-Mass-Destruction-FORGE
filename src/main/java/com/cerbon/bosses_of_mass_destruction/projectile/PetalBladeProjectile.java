package com.cerbon.bosses_of_mass_destruction.projectile;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import com.cerbon.bosses_of_mass_destruction.projectile.util.ExemptEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class PetalBladeProjectile extends BaseThrownItemProjectile{
    private Consumer<LivingEntity> entityHit;

    public static final DataParameter<Float> renderRotation = EntityDataManager.defineId(GauntletEntity.class, DataSerializers.FLOAT);

    public PetalBladeProjectile(EntityType<? extends ProjectileItemEntity> entityType, World level) {
        super(entityType, level);
        getEntityData().define(renderRotation, 0f);
    }

    public PetalBladeProjectile(LivingEntity livingEntity, World level, Consumer<LivingEntity> entityHit, List<EntityType<?>> exemptEntities, float rotation){
        super(BMDEntities.PETAL_BLADE.get(), livingEntity, level, new ExemptEntities(exemptEntities));
        this.entityHit = entityHit;
        getEntityData().define(renderRotation, rotation);
    }

    @Override
    public void entityHit(EntityRayTraceResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity owner = getOwner();

        if (owner instanceof LivingEntity){
            entity.hurt(
                    DamageSource.thrown(this, owner),
                    (float) ((LivingEntity) owner).getAttributeValue(Attributes.ATTACK_DAMAGE)
            );

            if (entity instanceof LivingEntity)
                if (entityHit != null)
                    entityHit.accept((LivingEntity) entity);
        }
        remove();
    }

    @Override
    protected void onHitBlock(@Nonnull BlockRayTraceResult result) {
        super.onHitBlock(result);
        remove();
    }
}
