package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendParticleS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class VoidLilyBlockEntity extends TileEntity implements ITickableTileEntity {
    private Vector3d structureDirection = null;

    public VoidLilyBlockEntity() {
        super(BMDBlockEntities.VOID_LILY_BLOCK_ENTITY.get());
    }

    @Override
    public void load(BlockState blockState, CompoundNBT tag) {
        if (tag.contains("dirX"))
            structureDirection = new Vector3d(tag.getDouble("dirX"), tag.getDouble("dirY"), tag.getDouble("dirZ"));

        super.load(blockState, tag);
    }

    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tag) {
        Vector3d dir = structureDirection;
        if (dir != null){
            tag.putDouble("dirX", dir.x);
            tag.putDouble("dirY", dir.y);
            tag.putDouble("dirZ", dir.z);
        }
        return super.save(tag);
    }

    @Override
    public void tick() {
        if (RandomUtils.range(0, 30) == 0 && level instanceof ServerWorld) {
            Vector3d direction = this.structureDirection;
            if (direction == null)
                setNearestStructureDirection((ServerWorld) level, getBlockPos(), this);

            if (direction != null)
                BMDPacketHandler.sendToAllPlayersTrackingChunk(new SendParticleS2CPacket(getBlockPos(), direction), (ServerWorld) level, VecUtils.asVec3(getBlockPos()));
        }
    }

    //TODO: Fix here
    private static void setNearestStructureDirection(ServerWorld level, BlockPos pos, VoidLilyBlockEntity entity){
//        BlockPos blockPos = level.findNearestMapFeature(BMDStructures.VOID_LILY_STRUCTURE_KEY, pos, 50, false);
//        if (blockPos != null)
//            entity.structureDirection = VecUtils.asVec3(new BlockPos(blockPos.getX(), 0, blockPos.getZ()).subtract(pos)).normalize();
//        else
//            entity.structureDirection = VecUtils.yAxis;
    }

    @OnlyIn(Dist.CLIENT)
    public static void spawnVoidLilyParticles(ClientWorld level, Vector3d pos, Vector3d dir){
        Vector3d streakPos = pos.add(new Vector3d(0.5, 0.7, 0.5)).add(RandomUtils.randVec().scale(0.5));
        Vector3d right = dir.cross(VecUtils.yAxis).normalize();
        double sinCurve = RandomUtils.range(8.0, 11.0) * RandomUtils.randSign();
        double curveLength = RandomUtils.range(1.2, 1.6);
        int particleAmount = RandomUtils.range(7, 10);

        BMDCapabilities.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> {
                            Vector3d particlePos = streakPos.add(RandomUtils.randVec().scale(0.05));
                            Particles.pollenParticles.continuousPosition(p -> {
                                float age = p.ageRatio;
                                Vector3d forward = dir.scale((double) age * curveLength);
                                Vector3d sinSide = right.scale(Math.sin(age * sinCurve) * 0.1);
                                return particlePos.add(forward).add(sinSide);
                            }).build(particlePos, Vector3d.ZERO);
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
