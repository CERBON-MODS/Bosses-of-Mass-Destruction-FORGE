package com.cerbon.bosses_of_mass_destruction.forge.mixin.test;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Class used to test if forge only mixins are being applied
@Mixin(Minecraft.class)
public abstract class TestMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void sendMessageIfWorking(GameConfig gameConfig, CallbackInfo ci) {
        BMDConstants.LOGGER.info("Forge only mixins are working for {}!",  BMDConstants.MOD_NAME);
    }
}