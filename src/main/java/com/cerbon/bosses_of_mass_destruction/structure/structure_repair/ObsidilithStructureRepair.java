package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEffectHandler;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithUtils;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.ObsidilithReviveS2CPacket;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.static_utilities.SoundUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import com.cerbon.cerbons_api.capability.CerbonsApiCapabilities;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ObsidilithStructureRepair implements StructureRepair{

    @Override
    public ResourceKey<Structure> associatedStructure() {
        return BMDStructures.OBSIDILITH_STRUCTURE_REGISTRY.getConfiguredStructureKey();
    }

    @Override
    public void repairStructure(ServerLevel level, StructureStart structureStart) {
        BlockPos topCenter = getTopCenter(structureStart);
        EventScheduler levelEventScheduler = CerbonsApiCapabilities.getLevelEventScheduler(level);
        BMDPacketHandler.sendToAllPlayersTrackingChunk(new ObsidilithReviveS2CPacket(VecUtils.asVec3(topCenter).add(0.5, 0.5, 0.5)), level, VecUtils.asVec3(topCenter).add(0.5, 0.5, 0.5));

        for (int y = 0; y <= ObsidilithUtils.deathPillarHeight; y++){
            int y1 = y;
            levelEventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                level.playSound(null, topCenter, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0f, SoundUtils.randomPitch(level.random));

                                for (Vec3 pos : ObsidilithUtils.circlePos)
                                    level.removeBlock(new BlockPos((int) pos.x, y1, (int) pos.z).offset(topCenter), false);

                                if (y1 == 0)
                                    level.setBlockAndUpdate(topCenter, BMDBlocks.OBSIDILITH_SUMMON_BLOCK.get().defaultBlockState());
                            },
                            y * ObsidilithUtils.ticksBetweenPillarLayer
                    )
            );
        }
    }

    @Override
    public boolean shouldRepairStructure(ServerLevel level, StructureStart structureStart) {
        BlockPos topCenter = getTopCenter(structureStart);
        boolean noBoss = level.getEntities(BMDEntities.OBSIDILITH.get(), obsidilithEntity -> obsidilithEntity.distanceToSqr(VecUtils.asVec3(topCenter)) < 100 * 100).isEmpty();
        boolean hasAltar = level.getBlockState(topCenter).getBlock() == BMDBlocks.OBSIDILITH_SUMMON_BLOCK.get();
        return noBoss && !hasAltar;
    }

    private BlockPos getTopCenter(StructureStart structureStart){
        BlockPos centerPos = structureStart.getBoundingBox().getCenter();
        return new BlockPos(centerPos.getX(), structureStart.getBoundingBox().maxY(), centerPos.getZ());
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleObsidilithRevivePacket(Vec3 pos, ClientLevel level){
        ObsidilithEffectHandler.spawnPillarParticles(pos, CerbonsApiCapabilities.getLevelEventScheduler(level));
    }
}
