package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.packet.custom.VoidBlossomReviveS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern.BossBlockDecorator;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.network.Dispatcher;
import com.cerbon.cerbons_api.api.static_utilities.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;

public class VoidBlossomStructureRepair implements StructureRepair{
    private static final ClientParticleBuilder spikeParticleFactory = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
            .color(Vec3Colors.VOID_PURPLE)
            .colorVariation(0.25)
            .brightness(BMDParticles.FULL_BRIGHT)
            .scale(f -> 0.5f * (1 - f * 0.25f))
            .age(20);

    @Override
    public ResourceKey<Structure> associatedStructure() {
        return BMDStructures.VOID_BLOSSOM_STRUCTURE_REGISTRY.getConfiguredStructureKey();
    }

    @Override
    public void repairStructure(ServerLevel level, StructureStart structureStart) {
        BlockPos offset = getCenterSpawn(structureStart, level);
        Dispatcher.sendToClientsLoadingPos(new VoidBlossomReviveS2CPacket(VecUtils.asVec3(offset)), level, VecUtils.asVec3(offset));

        CapabilityUtils.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> level.setBlockAndUpdate(offset, BMDBlocks.VOID_BLOSSOM_SUMMON_BLOCK.get().defaultBlockState()),
                        60
                )
        );
    }

    @Override
    public boolean shouldRepairStructure(ServerLevel level, StructureStart structureStart) {
        BlockPos centerPos = getCenterSpawn(structureStart, level);
        return level.getEntities(BMDEntities.VOID_BLOSSOM.get(), voidBlossomEntity -> voidBlossomEntity.distanceToSqr(VecUtils.asVec3(centerPos)) < 100 * 100).isEmpty();
    }

    private BlockPos getCenterSpawn(StructureStart structureStart, ServerLevel level){
        BlockPos pos = structureStart.getBoundingBox().getCenter();
        return BossBlockDecorator.bossBlockOffset(pos, level.getMinBuildHeight());
    }

    @Environment(EnvType.CLIENT)
    public static void handleVoidBlossomRevivePacket(Vec3 pos, ClientLevel level){
        CapabilityUtils.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> ParticleUtils.spawnRotatingParticles(
                                new ParticleUtils.RotatingParticles(
                                        pos.add(VecUtils.yAxis.scale(RandomUtils.range(1.0, 10.0))),
                                        spikeParticleFactory,
                                        1.0,
                                        2.0,
                                        3.0,
                                        4.0
                                )
                        ),
                        0,
                        50,
                        () -> false
                )
        );

        MathUtils.buildBlockCircle(2.3).forEach(vec3 ->
                new ClientParticleBuilder(BMDParticles.VOID_BLOSSOM_SPIKE_INDICATOR.get())
                        .age(60)
                        .build(pos.add(0.0, 0.1, 0.0).add(vec3), Vec3.ZERO));
    }
}
