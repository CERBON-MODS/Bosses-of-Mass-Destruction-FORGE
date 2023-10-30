package com.cerbon.bosses_of_mass_destruction.projectile.comet;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.projectile.BaseThrownItemProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.util.ExemptEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class CometProjectile extends BaseThrownItemProjectile implements GeoEntity {
    private boolean impacted = false;
    private Consumer<Vec3> impactAction;
    private final AnimatableInstanceCache animationFactory = GeckoLibUtil.createInstanceCache(this);

    public CometProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected CometProjectile(LivingEntity livingEntity, Level level, Consumer<Vec3> impactAction, List<EntityType<?>> exemptEntities){
        super(BMDEntities.COMET.get(), livingEntity, level, new ExemptEntities(exemptEntities));
        this.impactAction = impactAction;
    }

    @Override
    public void entityHit(EntityHitResult entityHitResult) {
        onImpact();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        onImpact();
        super.onHitBlock(result);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
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
            discard();
        }
    }

    @Override
    public boolean canCollideWith(@NotNull Entity entity) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationFactory;
    }

    @Override
    public float getXRot() {
        return tickCount * 5f;
    }
}
