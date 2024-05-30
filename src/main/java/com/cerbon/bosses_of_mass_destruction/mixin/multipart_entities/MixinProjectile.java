package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartAwareEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ProjectileEntity.class)
public abstract class MixinProjectile extends Entity {

    public MixinProjectile(EntityType<?> type, World level) {
        super(type, level);
    }

    @Inject(method = "onHit", at = @At("HEAD"))
    private void onCollision(RayTraceResult hitResult, CallbackInfo ci) {
        RayTraceResult.Type type = hitResult.getType();
        if (type == RayTraceResult.Type.ENTITY && hitResult instanceof EntityRayTraceResult) {
            Entity target = ((EntityRayTraceResult) hitResult).getEntity();
            if (target instanceof MultipartAwareEntity) {
                MultipartAwareEntity multipartAwareEntity = (MultipartAwareEntity) target;

                String nextDamagedPart = multipartAwareEntity.getBounds().raycast(position(), position().add(getDeltaMovement()));
                multipartAwareEntity.setNextDamagedPart(nextDamagedPart);
            }
        }
    }
}
