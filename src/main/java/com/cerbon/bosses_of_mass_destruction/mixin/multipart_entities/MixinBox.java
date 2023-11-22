package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.CompoundOrientedBox;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AABB.class)
public class MixinBox {

    @Inject(method = "intersects(Lnet/minecraft/world/phys/AABB;)Z", at = @At("HEAD"), cancellable = true)
    private void hook(final AABB box, final CallbackInfoReturnable<Boolean> cir) {
        if (box instanceof CompoundOrientedBox)
            cir.setReturnValue(box.intersects((AABB) (Object) this));
    }
}
