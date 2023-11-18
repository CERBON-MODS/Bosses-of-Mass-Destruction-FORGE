package com.cerbon.bosses_of_mass_destruction.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

public class StructureRegister {
    private final ResourceKey<Structure> configuredStructureKey;

    public StructureRegister(ResourceLocation structureResourceLocation) {
        this.configuredStructureKey = createConfigureStructureKey(structureResourceLocation);
    }

    private ResourceKey<Structure> createConfigureStructureKey(ResourceLocation resourceLocation) {
        return ResourceKey.create(Registries.STRUCTURE, resourceLocation);
    }

    public ResourceKey<Structure> getConfiguredStructureKey() {
        return this.configuredStructureKey;
    }
}

