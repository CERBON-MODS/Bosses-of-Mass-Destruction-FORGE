package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.CompoundOrientedBox;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VoxelShapes.class)
public class MixinVoxelShapes {
    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void hook(final AxisAlignedBB box, final CallbackInfoReturnable<VoxelShape> cir) {
        if (box instanceof CompoundOrientedBox)
            cir.setReturnValue(((CompoundOrientedBox) box).toVoxelShape());
    }
}
