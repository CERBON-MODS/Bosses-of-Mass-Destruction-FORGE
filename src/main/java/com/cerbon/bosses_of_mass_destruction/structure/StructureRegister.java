package com.cerbon.bosses_of_mass_destruction.structure;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructureRegister {
    private final RegistryKey<StructureFeature<?, ?>> configuredStructureKey;

    public StructureRegister(ResourceLocation structureResourceLocation) {
        this.configuredStructureKey = createConfigureStructureKey(structureResourceLocation);
    }

    private RegistryKey<StructureFeature<?, ?>> createConfigureStructureKey(ResourceLocation resourceLocation) {
        return RegistryKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, resourceLocation);
    }

    public RegistryKey<StructureFeature<?, ?>> getConfiguredStructureKey() {
        return this.configuredStructureKey;
    }
}

