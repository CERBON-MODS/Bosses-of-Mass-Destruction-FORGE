package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.shapes.VoxelShapeArray;
import net.minecraft.util.math.shapes.VoxelShapePart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VoxelShapeArray.class)
public interface InvokerArrayVoxelShape {

    @Invoker(value = "<init>")
    static VoxelShapeArray init(final VoxelShapePart shape, final DoubleList xPoints, final DoubleList yPoints, final DoubleList zPoints) {
        throw new AssertionError();
    }
}
