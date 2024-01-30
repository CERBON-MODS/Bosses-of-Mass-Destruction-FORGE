package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.cerbons_api.api.static_utilities.ParticleUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class GauntletStructureRepair implements StructureRepair{

    @Override
    public ResourceKey<Structure> associatedStructure() {
        return BMDStructures.GAUNTLET_STRUCTURE_REGISTRY.getConfiguredStructureKey();
    }

    @Override
    public void repairStructure(ServerLevel level, StructureStart structureStart) {
        BlockPos pos = runeCenter(structureStart);

        ParticleUtils.spawnParticle(level, BMDParticles.GAUNTLET_REVIVE_SPARKLES.get(), VecUtils.asVec3(pos.above(5)), VecUtils.unit.scale(3.0), 100, 0);

        spawnBlocks(level, pos);
    }

    @Override
    public boolean shouldRepairStructure(ServerLevel level, StructureStart structureStart) {
        BlockPos pos = runeCenter(structureStart);
        boolean hasSealAlready = level.getBlockState(pos.above()).getBlock() == BMDBlocks.GAUNTLET_BLACKSTONE.get();
        boolean noBoss = level.getEntities(BMDEntities.GAUNTLET.get(), entity -> entity.distanceToSqr(VecUtils.asVec3(pos)) < 100 * 100).isEmpty();
        return !hasSealAlready && noBoss;
    }

    private void spawnBlocks(ServerLevel level, BlockPos pos){
        for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++)
                for (int y = 0; y <= 4; y++)
                    level.setBlockAndUpdate(pos.offset(x, y, z), BMDBlocks.SEALED_BLACKSTONE.get().defaultBlockState());

        BlockPos up = pos.above();
        BlockState seal = BMDBlocks.GAUNTLET_BLACKSTONE.get().defaultBlockState();
        level.setBlockAndUpdate(up, seal);
        level.setBlockAndUpdate(up.east(), seal);
        level.setBlockAndUpdate(up.north(), seal);
        level.setBlockAndUpdate(up.west(), seal);
        level.setBlockAndUpdate(up.south(), seal);
    }

    private BlockPos runeCenter(StructureStart structureStart){
        return structureStart.getBoundingBox().getCenter().below(10);
    }
}
