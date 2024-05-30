package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VoidBlossomStructureRepair implements StructureRepair{
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
//
//    @Override
//    public RegistryKey<StructureFeature<?, ?>> associatedStructure() {
//        return BMDStructures.VOID_BLOSSOM_STRUCTURE_REGISTRY.getConfiguredStructureKey();
//    }
//
//    @Override
//    public void repairStructure(ServerWorld level, StructureStart structureStart) {
//        BlockPos offset = getCenterSpawn(structureStart, level);
//        BMDPacketHandler.sendToAllPlayersTrackingChunk(new VoidBlossomReviveS2CPacket(VecUtils.asVec3(offset)), level, VecUtils.asVec3(offset));
//
//        BMDCapabilities.getLevelEventScheduler(level).addEvent(
//                new TimedEvent(
//                        () -> level.setBlockAndUpdate(offset, BMDBlocks.VOID_BLOSSOM_SUMMON_BLOCK.get().defaultBlockState()),
//                        60
//                )
//        );
//    }
//
//    @Override
//    public boolean shouldRepairStructure(ServerWorld level, StructureStart structureStart) {
//        BlockPos centerPos = getCenterSpawn(structureStart, level);
//        return level.getEntities(BMDEntities.VOID_BLOSSOM.get(), voidBlossomEntity -> voidBlossomEntity.distanceToSqr(VecUtils.asVec3(centerPos)) < 100 * 100).isEmpty();
//    }
//
//    private BlockPos getCenterSpawn(StructureStart structureStart, ServerWorld level){
//        BlockPos pos = structureStart.getBoundingBox().getCenter();
//        return BossBlockDecorator.bossBlockOffset(pos, level.getMinBuildHeight());
//    }
//
    @OnlyIn(Dist.CLIENT)
    public static void handleVoidBlossomRevivePacket(Vector3d pos, ClientWorld level){
        BMDCapabilities.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> BMDUtils.spawnRotatingParticles(
                                new BMDUtils.RotatingParticles(
                                        pos.add(VecUtils.yAxis.scale(RandomUtils.range(1.0, 10.0))),
                                        Particles.spikeParticleFactory,
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
                        .build(pos.add(0.0, 0.1, 0.0).add(vec3), Vector3d.ZERO));
    }

    private static class Particles {
        private static final ClientParticleBuilder spikeParticleFactory = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
                .color(BMDColors.VOID_PURPLE)
                .colorVariation(0.25)
                .brightness(BMDParticles.FULL_BRIGHT)
                .scale(f -> 0.5f * (1 - f * 0.25f))
                .age(20);
    }
}
