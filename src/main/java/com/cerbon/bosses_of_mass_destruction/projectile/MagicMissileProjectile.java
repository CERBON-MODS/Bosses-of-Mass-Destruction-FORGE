package com.cerbon.bosses_of_mass_destruction.projectile;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.projectile.util.ExemptEntities;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class MagicMissileProjectile extends BaseThrownItemProjectile {
    private Consumer<LivingEntity> entityHit;

    public MagicMissileProjectile(EntityType<? extends ProjectileItemEntity> entityType, World level) {
        super(entityType, level);
    }

    public MagicMissileProjectile(LivingEntity livingEntity, World level, Consumer<LivingEntity> entityHit, List<EntityType<?>> exemptEntities) {
        super(BMDEntities.MAGIC_MISSILE.get(), livingEntity, level, new ExemptEntities(exemptEntities));
        this.entityHit = entityHit;
    }

    @Override
    public void entityHit(EntityRayTraceResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity owner = getOwner();

        if (owner instanceof LivingEntity){
            entity.hurt(
                    DamageSource.thrown(this, owner),
                    (float) ((LivingEntity)owner).getAttributeValue(Attributes.ATTACK_DAMAGE)
            );

            if (entity instanceof LivingEntity)
                if (entityHit != null)
                    entityHit.accept((LivingEntity) entity);
        }
        remove();
    }

    @Override
    protected void onHitBlock(@Nonnull BlockRayTraceResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        playSound(BMDSounds.BLUE_FIREBALL_LAND.get(), 1.0f, 1.0f);
        remove();
    }
}
