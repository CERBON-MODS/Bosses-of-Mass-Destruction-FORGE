package com.cerbon.bosses_of_mass_destruction.structure;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

public class StructureRegister {
    private final ResourceKey<ConfiguredStructureFeature<?, ?>> configuredStructureKey;

    public StructureRegister(ResourceLocation structureResourceLocation) {
        this.configuredStructureKey = createConfigureStructureKey(structureResourceLocation);
    }

    private ResourceKey<ConfiguredStructureFeature<?, ?>> createConfigureStructureKey(ResourceLocation resourceLocation) {
        return ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, resourceLocation);
    }

    public ResourceKey<ConfiguredStructureFeature<?, ?>> getConfiguredStructureKey() {
        return this.configuredStructureKey;
    }
}

