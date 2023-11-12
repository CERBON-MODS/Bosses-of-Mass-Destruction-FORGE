package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArrayVoxelShape.class)
public interface InvokerArrayVoxelShape {

    @Invoker(value = "<init>")
    static ArrayVoxelShape init(final DiscreteVoxelShape shape, final DoubleList xPoints, final DoubleList yPoints, final DoubleList zPoints) {
        throw new AssertionError();
    }
}
