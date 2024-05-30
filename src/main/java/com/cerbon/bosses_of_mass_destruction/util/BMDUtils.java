package com.cerbon.bosses_of_mass_destruction.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.BlockVoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class BMDUtils {
    public static void addDeltaMovement(Entity entity, Vector3d velocity){
        entity.setDeltaMovement(entity.getDeltaMovement().add(velocity));
    }

    public static void spawnParticle(ServerWorld level, IParticleData particleType, Vector3d pos, Vector3d velOrOffset, int count, double speed) {
        level.sendParticles(
                particleType,
                pos.x,
                pos.y,
                pos.z,
                count,
                velOrOffset.x,
                velOrOffset.y,
                velOrOffset.z,
                speed
        );
    }

    public static void playSound(
            ServerWorld level,
            Vector3d pos,
            SoundEvent soundEvent,
            SoundCategory soundSource,
            float volume,
            float pitch,
            double range,
            PlayerEntity player
    ) {
        level.getServer().getPlayerList().broadcast(
                player,
                pos.x,
                pos.y,
                pos.z,
                range,
                level.dimension(),
                new SPlaySoundEffectPacket(soundEvent, soundSource, pos.x, pos.y, pos.z, volume, pitch)
        );
    }

    public static void playSound(
            ServerWorld level,
            Vector3d pos,
            SoundEvent soundEvent,
            SoundCategory soundSource,
            float volume,
            double range,
            PlayerEntity player
    ) {
        playSound(level, pos, soundEvent, soundSource, volume, randomPitch(level.random), range, player);
    }

    public static float randomPitch(@Nonnull Random random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f;
    }

    public static BlockPos findGroundBelow(World level, BlockPos pos, Function<BlockPos, Boolean> isOpenBlock) {
        int bottomY = 0;
        for (int i = pos.getY(); i >= bottomY + 1; i--) {
            BlockPos tempPos = new BlockPos(pos.getX(), i, pos.getZ());

            if (level.getBlockState(tempPos).isFaceSturdy(level, tempPos, Direction.UP, BlockVoxelShape.FULL) && isOpenBlock.apply(tempPos.above()))
                return tempPos;
        }
        return new BlockPos(pos.getX(), bottomY, pos.getZ());
    }

    public static void preventDespawnExceptPeaceful(MobEntity mobEntity, World level){
        if (level.getDifficulty() == Difficulty.PEACEFUL)
            mobEntity.remove();
        else
            mobEntity.setNoActionTime(0);
    }

    public static List<Entity> findEntitiesInLine(World level, Vector3d start, Vector3d end, Entity toExclude) {
        AxisAlignedBB aabb = new AxisAlignedBB(start, end);
        return level.getEntities(toExclude, aabb, entity -> entity.getBoundingBox().clip(start, end).isPresent());
    }

    public static class RotatingParticles {
        public final Vector3d pos;
        public final ClientParticleBuilder particleBuilder;
        public final double minRadius;
        public final double maxRadius;
        public final double minSpeed;
        public final double maxSpeed;

        public RotatingParticles(Vector3d pos, ClientParticleBuilder particleBuilder, double minRadius, double maxRadius, double minSpeed, double maxSpeed) {
            this.pos = pos;
            this.particleBuilder = particleBuilder;
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
            this.minSpeed = minSpeed;
            this.maxSpeed = maxSpeed;
        }
    }

    public static void spawnRotatingParticles(RotatingParticles particleParams) {
        int startingRotation = new Random().nextInt(360);
        double randomRadius = RandomUtils.range(particleParams.minRadius, particleParams.maxRadius);
        double rotationSpeed = RandomUtils.range(particleParams.minSpeed, particleParams.maxSpeed);
        ClientParticleBuilder builder = particleParams.particleBuilder;
        builder.continuousPosition(
                age -> rotateAroundPos(particleParams.pos, age.getAge(), startingRotation, randomRadius, rotationSpeed))
                .build(rotateAroundPos(particleParams.pos, 0, startingRotation, randomRadius, rotationSpeed), Vector3d.ZERO);
    }

    private static Vector3d rotateAroundPos(Vector3d pos, int age, int startingRotation, double radius, double rotationSpeed) {
        Vector3d xzOffset = VecUtils.xAxis.yRot((float)Math.toRadians(age * rotationSpeed + startingRotation));
        return pos.add(xzOffset.scale(radius));
    }
}
