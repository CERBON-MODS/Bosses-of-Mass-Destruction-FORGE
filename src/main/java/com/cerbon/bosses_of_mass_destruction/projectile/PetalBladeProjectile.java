package com.cerbon.bosses_of_mass_destruction.projectile;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import com.cerbon.bosses_of_mass_destruction.projectile.util.ExemptEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class PetalBladeProjectile extends BaseThrownItemProjectile{
    private Consumer<LivingEntity> entityHit;

    public static final EntityDataAccessor<Float> renderRotation = SynchedEntityData.defineId(GauntletEntity.class, EntityDataSerializers.FLOAT);

    public PetalBladeProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        getEntityData().define(renderRotation, 0f);
    }

    public PetalBladeProjectile(LivingEntity livingEntity, Level level, Consumer<LivingEntity> entityHit, List<EntityType<?>> exemptEntities, float rotation){
        super(BMDEntities.PETAL_BLADE.get(), livingEntity, level, new ExemptEntities(exemptEntities));
        this.entityHit = entityHit;
        getEntityData().define(renderRotation, rotation);
    }

    @Override
    public void entityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity owner = getOwner();

        if (owner instanceof LivingEntity livingEntity){
            entity.hurt(
                    entity.level().damageSources().thrown(this, owner),
                    (float) livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE)
            );

            if (entity instanceof LivingEntity)
                if (entityHit != null)
                    entityHit.accept((LivingEntity) entity);
        }
        discard();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        discard();
    }
}
