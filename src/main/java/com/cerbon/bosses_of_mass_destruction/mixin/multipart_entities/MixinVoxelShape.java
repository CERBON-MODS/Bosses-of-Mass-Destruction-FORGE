package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.CompoundOrientedBox;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VoxelShape.class)
public class MixinVoxelShape {
    @Inject(method = "collide", at = @At("HEAD"), cancellable = true)
    private void hook(final Direction.Axis axis, final AxisAlignedBB box, final double maxDist, final CallbackInfoReturnable<Double> cir) {
        if (box instanceof CompoundOrientedBox)
            cir.setReturnValue(((CompoundOrientedBox) box).calculateMaxDistance(axis, (VoxelShape) (Object) this, maxDist));
    }
}
