package com.cerbon.bosses_of_mass_destruction.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class BMDUtils {
    public static void spawnParticle(ServerLevel level, ParticleOptions particleType, Vec3 pos, Vec3 velOrOffset, int count, double speed) {
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

    public static DamageSource shieldPiercing(Level level, Entity attacker) {
        return VanillaCopies.create(level, ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(BMDConstants.MOD_ID, "shield_piercing")), attacker);
    }

    public static void playSound(
            ServerLevel level,
            Vec3 pos,
            SoundEvent soundEvent,
            SoundSource soundSource,
            float volume,
            float pitch,
            double range,
            Player player
    ) {
        Holder<SoundEvent> holder = Holder.direct(SoundEvent.createVariableRangeEvent(soundEvent.getLocation()));
        level.getServer().getPlayerList().broadcast(
                player,
                pos.x,
                pos.y,
                pos.z,
                range,
                level.dimension(),
                new ClientboundSoundPacket(holder, soundSource, pos.x, pos.y, pos.z, volume, pitch, level.random.nextLong())
        );
    }

    public static void playSound(
            ServerLevel level,
            Vec3 pos,
            SoundEvent soundEvent,
            SoundSource soundSource,
            float volume,
            double range,
            Player player
    ) {
        playSound(level, pos, soundEvent, soundSource, volume, randomPitch(level.random), range, player);
    }

    public static float randomPitch(@NotNull RandomSource random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f;
    }

    public static BlockPos findGroundBelow(Level level, BlockPos pos, Function<BlockPos, Boolean> isOpenBlock) {
        int bottomY = level.getMinBuildHeight();
        for (int i = pos.getY(); i >= bottomY + 1; i--) {
            BlockPos tempPos = new BlockPos(pos.getX(), i, pos.getZ());

            if (level.getBlockState(tempPos).isFaceSturdy(level, tempPos, Direction.UP, SupportType.FULL) && isOpenBlock.apply(tempPos.above()))
                return tempPos;
        }
        return new BlockPos(pos.getX(), bottomY, pos.getZ());
    }

    public static void preventDespawnExceptPeaceful(LivingEntity mobEntity, Level level){
        if (level.getDifficulty() == Difficulty.PEACEFUL)
            mobEntity.discard();
        else
            mobEntity.setNoActionTime(0);
    }

    public static List<Entity> findEntitiesInLine(Level level, Vec3 start, Vec3 end, Entity toExclude) {
        AABB aabb = new AABB(start, end);
        return level.getEntities(toExclude, aabb, entity -> entity.getBoundingBox().clip(start, end).isPresent());
    }

    public record RotatingParticles(Vec3 pos, ClientParticleBuilder particleBuilder, double minRadius, double maxRadius, double minSpeed, double maxSpeed){}

    public static void spawnRotatingParticles(RotatingParticles particleParams) {
        int startingRotation = new Random().nextInt(360);
        double randomRadius = RandomUtils.range(particleParams.minRadius(), particleParams.maxRadius());
        double rotationSpeed = RandomUtils.range(particleParams.minSpeed(), particleParams.maxSpeed());
        ClientParticleBuilder builder = particleParams.particleBuilder();
        builder.continuousPosition(
                age -> rotateAroundPos(particleParams.pos(), age.getAge(), startingRotation, randomRadius, rotationSpeed))
                .build(rotateAroundPos(particleParams.pos(), 0, startingRotation, randomRadius, rotationSpeed), Vec3.ZERO);
    }

    private static Vec3 rotateAroundPos(Vec3 pos, int age, int startingRotation, double radius, double rotationSpeed) {
        Vec3 xzOffset = VecUtils.xAxis.yRot((float)Math.toRadians(age * rotationSpeed + startingRotation));
        return pos.add(xzOffset.multiply(radius, radius, radius));
    }

    public static ConfiguredFeature<?, ?> getConfiguredFeature(LevelReader levelReader, ResourceKey<ConfiguredFeature<?, ?>> key) {
        return levelReader.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(key);
    }

}
