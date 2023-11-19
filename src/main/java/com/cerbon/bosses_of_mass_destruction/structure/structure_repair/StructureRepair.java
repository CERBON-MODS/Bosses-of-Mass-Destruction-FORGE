package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface StructureRepair {
    ResourceKey<Structure> associatedStructure();
    void repairStructure(ServerLevel level, StructureStart structureStart);
    boolean shouldRepairStructure(ServerLevel level, StructureStart structureStart);
}
