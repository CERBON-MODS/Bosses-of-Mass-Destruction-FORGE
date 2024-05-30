package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.damagesource.UnshieldableDamageSource;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "isDamageSourceBlocked", at = @At("HEAD"), cancellable = true)
    private void handleCustomUnshieldableDamageSource(DamageSource source, CallbackInfoReturnable<Boolean> cir){
        if (source instanceof UnshieldableDamageSource)
            cir.setReturnValue(false);
    }
}
