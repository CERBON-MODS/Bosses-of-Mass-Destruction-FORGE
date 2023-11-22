package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartAwareEntity;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void getBoundingBox(CallbackInfoReturnable<AABB> cir) {
        if (this instanceof MultipartEntity) {
            cir.setReturnValue(((MultipartEntity) this).getCompoundBoundingBox(cir.getReturnValue()));
        }
    }

    @Inject(method = "setPosRaw", at = @At("TAIL"))
    private void setPos(double x, double y, double z, CallbackInfo ci) {
        if (this instanceof MultipartAwareEntity) {
            ((MultipartAwareEntity)this).onSetPos(x, y, z);
        }
    }
}
