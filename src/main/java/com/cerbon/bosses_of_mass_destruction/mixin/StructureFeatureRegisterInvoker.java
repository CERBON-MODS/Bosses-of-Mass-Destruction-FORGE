package com.cerbon.bosses_of_mass_destruction.mixin;

import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureFeature.class)
public interface StructureFeatureRegisterInvoker {

    @Invoker("register")
    static <F extends StructureFeature<?>> F invokeRegister(String name, F structureFeature, GenerationStep.Decoration step) {
        throw new AssertionError();
    }
}
