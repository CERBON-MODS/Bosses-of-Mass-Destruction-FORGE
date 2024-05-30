package com.cerbon.bosses_of_mass_destruction.projectile.comet;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.projectile.BaseThrownItemProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.util.ExemptEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class CometProjectile extends BaseThrownItemProjectile implements IAnimatable, IAnimationTickable {
    private Consumer<Vector3d> impactAction;
    private boolean impacted = false;

    public CometProjectile(EntityType<? extends ProjectileItemEntity> entityType, World level) {
        super(entityType, level);
    }

    public CometProjectile(LivingEntity livingEntity, World level, Consumer<Vector3d> impactAction, List<EntityType<?>> exemptEntities){
        super(BMDEntities.COMET.get(), livingEntity, level, new ExemptEntities(exemptEntities));
        this.impactAction = impactAction;
    }

    @Override
    public void entityHit(EntityRayTraceResult entityHitResult) {
        onImpact();
    }

    @Override
    protected void onHitBlock(@Nonnull BlockRayTraceResult result) {
        onImpact();
        super.onHitBlock(result);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        onImpact();
        return super.hurt(source, amount);
    }

    private void onImpact(){
        if (impacted) return;
        impacted = true;
        Entity owner = getOwner();

        if (owner instanceof LivingEntity){
            if (impactAction != null)
                impactAction.accept(position());
            remove();
        }
    }

    @Override
    public boolean canCollideWith(@Nonnull Entity entity) {
        return true;
    }

    public float getXRot() {
        return tickCount * 5f;
    }

    @Override
    public void registerControllers(AnimationData animationData) {}

    @Override
    public AnimationFactory getFactory() {
        return new AnimationFactory(this);
    }

    @Override
    public int tickTimer() {
        return 0;
    }
}
