package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendParticleS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import com.cerbon.cerbons_api.capability.CerbonsApiCapabilities;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class VoidLilyBlockEntity extends BlockEntity {
    private Vec3 structureDirection = null;

    public VoidLilyBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMDBlockEntities.VOID_LILY_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("dirX"))
            structureDirection = new Vec3(tag.getDouble("dirX"), tag.getDouble("dirY"), tag.getDouble("dirZ"));

        super.load(tag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        Vec3 dir = structureDirection;
        if (dir != null){
            tag.putDouble("dirX", dir.x);
            tag.putDouble("dirY", dir.y);
            tag.putDouble("dirZ", dir.z);
        }
        super.saveAdditional(tag);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VoidLilyBlockEntity entity){
        if (RandomUtils.range(0, 30) == 0 && level instanceof ServerLevel serverLevel) {
            Vec3 direction = entity.structureDirection;
            if (direction == null)
                setNearestStructureDirection(serverLevel, pos, entity);

            if (direction != null)
                BMDPacketHandler.sendToAllPlayersTrackingChunk(new SendParticleS2CPacket(pos, direction), serverLevel, VecUtils.asVec3(pos));
        }
    }

    private static void setNearestStructureDirection(ServerLevel level, BlockPos pos, VoidLilyBlockEntity entity){
        BlockPos blockPos = level.findNearestMapStructure(BMDStructures.VOID_LILY_STRUCTURE_KEY, pos, 50, false);
        if (blockPos != null)
            entity.structureDirection = VecUtils.asVec3(new BlockPos(blockPos.getX(), level.getMinBuildHeight(), blockPos.getZ()).subtract(pos)).normalize();
        else
            entity.structureDirection = VecUtils.yAxis;
    }

    @OnlyIn(Dist.CLIENT)
    public static void spawnVoidLilyParticles(ClientLevel level, Vec3 pos, Vec3 dir){
        Vec3 streakPos = pos.add(new Vec3(0.5, 0.7, 0.5)).add(RandomUtils.randVec().scale(0.5));
        Vec3 right = dir.cross(VecUtils.yAxis).normalize();
        double sinCurve = RandomUtils.range(8.0, 11.0) * RandomUtils.randSign();
        double curveLength = RandomUtils.range(1.2, 1.6);
        int particleAmount = RandomUtils.range(7, 10);

        CerbonsApiCapabilities.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> {
                            Vec3 particlePos = streakPos.add(RandomUtils.randVec().scale(0.05));
                            Particles.pollenParticles.continuousPosition(p -> {
                                float age = p.ageRatio;
                                Vec3 forward = dir.scale((double) age * curveLength);
                                Vec3 sinSide = right.scale(Math.sin(age * sinCurve) * 0.1);
                                return particlePos.add(forward).add(sinSide);
                            }).build(particlePos, Vec3.ZERO);
                        },
                        0,
                        particleAmount,
                        () -> false
                )
        );
    }

    private static class Particles {
        @OnlyIn(Dist.CLIENT) private static final ClientParticleBuilder pollenParticles = new ClientParticleBuilder(BMDParticles.POLLEN.get())
                .scale(age -> (float) (Math.sin(age * (float) Math.PI) * 0.04f))
                .age(30);
    }
}
