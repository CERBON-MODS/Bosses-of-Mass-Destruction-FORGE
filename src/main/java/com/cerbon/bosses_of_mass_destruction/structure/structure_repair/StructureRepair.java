package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface StructureRepair {
    RegistryKey<StructureFeature<?, ?>> associatedStructure();
    void repairStructure(ServerWorld level, StructureStart structureStart);
    boolean shouldRepairStructure(ServerWorld level, StructureStart structureStart);
}
