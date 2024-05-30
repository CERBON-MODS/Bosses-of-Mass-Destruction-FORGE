package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEffectHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ObsidilithStructureRepair implements StructureRepair{
    @Override
    public RegistryKey<StructureFeature<?, ?>> associatedStructure() {
        return null;
    }

    @Override
    public void repairStructure(ServerWorld level, StructureStart structureStart) {

    }

    @Override
    public boolean shouldRepairStructure(ServerWorld level, StructureStart structureStart) {
        return false;
    }

    //TODO: Fiz here
//    @Override
//    public RegistryKey<StructureFeature<?, ?>> associatedStructure() {
//        return BMDStructures.OBSIDILITH_STRUCTURE_REGISTRY.getConfiguredStructureKey();
//    }
//
//    @Override
//    public void repairStructure(ServerWorld level, StructureStart structureStart) {
//        BlockPos topCenter = getTopCenter(structureStart);
//        EventScheduler levelEventScheduler = BMDCapabilities.getLevelEventScheduler(level);
//        BMDPacketHandler.sendToAllPlayersTrackingChunk(new ObsidilithReviveS2CPacket(VecUtils.asVec3(topCenter).add(0.5, 0.5, 0.5)), level, VecUtils.asVec3(topCenter).add(0.5, 0.5, 0.5));
//
//        for (int y = 0; y <= ObsidilithUtils.deathPillarHeight; y++){
//            int y1 = y;
//            levelEventScheduler.addEvent(
//                    new TimedEvent(
//                            () -> {
//                                level.playSound(null, topCenter, SoundEvents.STONE_PLACE, SoundCategory.BLOCKS, 1.0f, BMDUtils.randomPitch(level.random));
//
//                                for (Vector3d pos : ObsidilithUtils.circlePos)
//                                    level.removeBlock(new BlockPos((int) pos.x, y1, (int) pos.z).offset(topCenter), false);
//
//                                if (y1 == 0)
//                                    level.setBlockAndUpdate(topCenter, BMDBlocks.OBSIDILITH_SUMMON_BLOCK.get().defaultBlockState());
//                            },
//                            y * ObsidilithUtils.ticksBetweenPillarLayer
//                    )
//            );
//        }
//    }
//
//    @Override
//    public boolean shouldRepairStructure(ServerWorld level, StructureStart structureStart) {
//        BlockPos topCenter = getTopCenter(structureStart);
//        boolean noBoss = level.getEntities(BMDEntities.OBSIDILITH.get(), obsidilithEntity -> obsidilithEntity.distanceToSqr(VecUtils.asVec3(topCenter)) < 100 * 100).isEmpty();
//        boolean hasAltar = level.getBlockState(topCenter).getBlock() == BMDBlocks.OBSIDILITH_SUMMON_BLOCK.get();
//        return noBoss && !hasAltar;
//    }
//
//    private BlockPos getTopCenter(StructureStart structureStart){
//        BlockPos centerPos = structureStart.getBoundingBox().getCenter();
//        return new BlockPos(centerPos.getX(), structureStart.getBoundingBox().maxY(), centerPos.getZ());
//    }
//
    @OnlyIn(Dist.CLIENT)
    public static void handleObsidilithRevivePacket(Vector3d pos, ClientWorld level){
        ObsidilithEffectHandler.spawnPillarParticles(pos, BMDCapabilities.getLevelEventScheduler(level));
    }
}
