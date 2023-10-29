package com.cerbon.bosses_of_mass_destruction.projectile;

import com.google.errorprone.annotations.ForOverride;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public abstract class BaseThrownItemProjectile extends ThrowableItemProjectile {
    protected Predicate<EntityHitResult> entityCollisionPredicate;
    protected Predicate<HitResult> collisionPredicate = hitResult -> !level().isClientSide();

    public BaseThrownItemProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        entityCollisionPredicate = entityHitResult -> true;
    }

    public BaseThrownItemProjectile(EntityType<? extends ThrowableItemProjectile> entityType, LivingEntity livingEntity, Level level, Predicate<EntityHitResult> collisionPredicate) {
        super(entityType, livingEntity, level);
        this.entityCollisionPredicate = collisionPredicate;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide)
            clientTick();
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        if(collisionPredicate.test(hitResult))
            super.onHit(hitResult);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        if (entityCollisionPredicate.test(entityHitResult))
            entityHit(entityHitResult);
    }

    abstract void entityHit(EntityHitResult entityHitResult);

    @ForOverride
    public void clientTick() {}

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.SNOWBALL;
    }
}