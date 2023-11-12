package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartAwareEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Projectile.class)
public abstract class MixinProjectile extends Entity {

    public MixinProjectile(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "onHit", at = @At("HEAD"))
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult) {
            Entity target = ((EntityHitResult) hitResult).getEntity();
            if (target instanceof MultipartAwareEntity multipartAwareEntity) {
                String nextDamagedPart = multipartAwareEntity.getBounds().raycast(position(), position().add(getDeltaMovement()));
                multipartAwareEntity.setNextDamagedPart(nextDamagedPart);
            }
        }
    }
}
