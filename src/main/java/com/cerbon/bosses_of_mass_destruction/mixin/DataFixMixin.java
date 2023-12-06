package com.cerbon.bosses_of_mass_destruction.mixin;

import net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StructuresBecomeConfiguredFix.class)
public interface DataFixMixin {

    @Mutable
    @Accessor("CONVERSION_MAP")
    static Map<String, StructuresBecomeConfiguredFix.Conversion> bmd_getStructures(){
        throw new AssertionError();
    }

    @Mutable
    @Accessor("CONVERSION_MAP")
    static void bmd_setStructures(Map<String, StructuresBecomeConfiguredFix.Conversion> mapping) {
        throw new AssertionError();
    }
}
