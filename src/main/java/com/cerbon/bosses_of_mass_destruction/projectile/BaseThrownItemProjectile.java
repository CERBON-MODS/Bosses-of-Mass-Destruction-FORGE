package com.cerbon.bosses_of_mass_destruction.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public abstract class BaseThrownItemProjectile extends ProjectileItemEntity {
    protected final Predicate<EntityRayTraceResult> entityCollisionPredicate;
    protected Predicate<RayTraceResult> collisionPredicate = hitResult -> !level.isClientSide();

    public BaseThrownItemProjectile(EntityType<? extends ProjectileItemEntity> entityType, World level) {
        super(entityType, level);
        entityCollisionPredicate = entityHitResult -> true;
    }

    public BaseThrownItemProjectile(EntityType<? extends ProjectileItemEntity> entityType, LivingEntity livingEntity, World level, Predicate<EntityRayTraceResult> collisionPredicate) {
        super(entityType, livingEntity, level);
        this.entityCollisionPredicate = collisionPredicate;
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide)
            clientTick();
    }

    @Override
    public @Nonnull IPacket<?> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }

    @Override
    protected void onHit(@Nonnull RayTraceResult hitResult) {
        if(collisionPredicate.test(hitResult))
            super.onHit(hitResult);
    }

    @Override
    protected void onHitEntity(@Nonnull EntityRayTraceResult entityHitResult) {
        if (entityCollisionPredicate.test(entityHitResult))
            entityHit(entityHitResult);
    }

    protected abstract void entityHit(EntityRayTraceResult entityHitResult);

    public void clientTick() {}

    @Override
    protected @Nonnull Item getDefaultItem() {
        return Items.SNOWBALL;
    }
}
