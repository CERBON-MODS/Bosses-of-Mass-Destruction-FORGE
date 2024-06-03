package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructuresFeature;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.block.BlockState;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

public class GauntletStructureRepair implements StructureRepair{

    @Override
    public RegistryKey<StructureFeature<?, ?>> associatedStructure() {
        return BMDStructuresFeature.GAUNTLET_REGISTRY.getConfiguredStructureKey();
    }

    @Override
    public void repairStructure(ServerWorld level, StructureStart structureStart) {
        BlockPos pos = runeCenter(structureStart);

        BMDUtils.spawnParticle(level, BMDParticles.GAUNTLET_REVIVE_SPARKLES.get(), VecUtils.asVec3(pos.above(5)), VecUtils.unit.scale(3.0), 100, 0);

        spawnBlocks(level, pos);
    }

    @Override
    public boolean shouldRepairStructure(ServerWorld level, StructureStart structureStart) {
        BlockPos pos = runeCenter(structureStart);
        boolean hasSealAlready = level.getBlockState(pos.above()).getBlock() == BMDBlocks.GAUNTLET_BLACKSTONE.get();
        boolean noBoss = level.getEntities(BMDEntities.GAUNTLET.get(), entity -> entity.distanceToSqr(VecUtils.asVec3(pos)) < 100 * 100).isEmpty();
        return !hasSealAlready && noBoss;
    }

    private void spawnBlocks(ServerWorld level, BlockPos pos){
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
        return new BlockPos(structureStart.getBoundingBox().getCenter().below(10));
    }
}
