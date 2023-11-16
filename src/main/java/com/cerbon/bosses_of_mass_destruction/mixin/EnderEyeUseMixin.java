package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.block.custom.ObsidilithSummonBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public class EnderEyeUseMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void onEyeUsed(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir){
        ObsidilithSummonBlock.onEnderEyeUsed(context, cir);
    }
}
