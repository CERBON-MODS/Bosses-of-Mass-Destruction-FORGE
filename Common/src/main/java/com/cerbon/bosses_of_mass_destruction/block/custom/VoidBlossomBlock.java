package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.EntityAdapter;
import com.cerbon.bosses_of_mass_destruction.entity.util.EntityStats;
import com.cerbon.bosses_of_mass_destruction.packet.custom.HealS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.network.Dispatcher;
import com.cerbon.cerbons_api.api.static_utilities.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VoidBlossomBlock extends Block {
    private static final int healAnimationDelay = 16;
    private static final int healDelay = 64;
    private final VoxelShape shape = box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

    public VoidBlossomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void tick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        level.scheduleTick(pos, this, healDelay);
        healNearbyEntities(level, pos);
    }

    private void healNearbyEntities(ServerLevel level, BlockPos pos){
        level.getEntitiesOfClass(VoidBlossomEntity.class, new AABB(pos).inflate(40.0, 20.0, 40.0))
                .forEach(voidBlossom -> {
                    CapabilityUtils.getLevelEventScheduler(level).addEvent(
                            new TimedEvent(
                                    () -> LichUtils.cappedHeal(new EntityAdapter(voidBlossom), new EntityStats(voidBlossom), VoidBlossomEntity.hpMilestones, 10f, voidBlossom::heal),
                                    healAnimationDelay
                            ));

                    Dispatcher.sendToClientsLoadingPos(new HealS2CPacket(VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5)), voidBlossom.position().add(VecUtils.yAxis.scale(5.0))), level, voidBlossom.position());
                });
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)){
            destroy(level, pos, state);
            return Blocks.AIR.defaultBlockState();
        }else
            return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return canSupportCenter(level, pos, Direction.UP) && !level.isWaterAt(pos);
    }

    @Override
    public void destroy(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state) {
        for(int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for(int y = -1; y <= 1; y ++) {
                    if((x != 0 || z != 0)) {
                        BlockPos pos1 = pos.offset(x, y, z);
                        if(level.getBlockState(pos1).getBlock() == BMDBlocks.VINE_WALL.get()) {
                            level.scheduleTick(pos1, BMDBlocks.VINE_WALL.get(), (2 - y) * 20 + RandomUtils.range(0, 19));
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return shape;
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (level.isClientSide) {
            for (int i = 0; i < 12; i++){
                Vec3 vel = VecUtils.yAxis.scale(RandomUtils.range(0.1, 0.2));
                Vec3 spawnPos = VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5)).add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(0.5));
                Particles.spikeParticleFactory.build(spawnPos, vel);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Environment(EnvType.CLIENT)
    public static void handleVoidBlossomHeal(ClientLevel level, Vec3 source, Vec3 dest){
        spawnHealParticle(dest, source, level);
        spawnChargeParticle(source, level);
    }

    @Environment(EnvType.CLIENT)
    private static void spawnChargeParticle(Vec3 source, ClientLevel level){
        Vec3 particlePos = source.add(VecUtils.yAxis.scale(0.25));
        CapabilityUtils.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> Particles.healParticleFactory.build(particlePos.add(RandomUtils.randVec().scale(0.2)), Vec3.ZERO),
                        32,
                        32,
                        () -> false
                )
        );
    }

    @Environment(EnvType.CLIENT)
    private static void spawnHealParticle(Vec3 dest, Vec3 source, ClientLevel level){
        List<Vec3> particlePositions = new ArrayList<>();
        int numCirclePoints = healAnimationDelay;
        List<Vec3> circlePoints = MathUtils.circlePoints(0.5, numCirclePoints, dest.subtract(source).normalize()).stream().toList();
        MathUtils.lineCallback(source, dest, 32, (pos, i) ->
                particlePositions.add(pos.add(circlePoints.get(i % numCirclePoints))));

        AtomicInteger i = new AtomicInteger();
        CapabilityUtils.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> {
                            Particles.healParticleFactory.build(particlePositions.get(i.get()), Vec3.ZERO);
                            Particles.healParticleFactory.build(particlePositions.get(i.get() + 1), Vec3.ZERO);
                            i.addAndGet(2);
                        },
                        0,
                        healAnimationDelay,
                        () -> false
                )
        );
    }

    public static void handleVoidBlossomPlace(Vec3 pos){
        for (int i = 0; i <= 12; i++){
            Vec3 spawnPos = pos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(0.5));
            Vec3 vel = VecUtils.yAxis.scale(RandomUtils.range(0.1, 0.3));
            int randomRot = RandomUtils.range(0, 360);
            float angularMomentum = RandomUtils.randSign() * 4f;

            Particles.petalParticleFactory
                    .continuousRotation(f -> randomRot + f.getAge() * angularMomentum)
                    .continuousVelocity(f -> vel.scale(1.0 - f.ageRatio))
                    .build(spawnPos, vel);
        }
    }

    public static class Particles {
        private static final ClientParticleBuilder spikeParticleFactory = new ClientParticleBuilder(BMDParticles.LINE.get())
                .color(f -> MathUtils.lerpVec(f, Vec3Colors.VOID_PURPLE, Vec3Colors.ULTRA_DARK_PURPLE))
                .colorVariation(0.15)
                .brightness(BMDParticles.FULL_BRIGHT)
                .scale(0.25f)
                .age(10, 15);

        private static final ClientParticleBuilder healParticleFactory = new ClientParticleBuilder(BMDParticles.OBSIDILITH_BURST.get())
                .color(f -> MathUtils.lerpVec(f, Vec3Colors.PINK, Vec3Colors.ULTRA_DARK_PURPLE))
                .colorVariation(0.15)
                .brightness(BMDParticles.FULL_BRIGHT)
                .scale(f -> 0.4f * (1 - f * 0.75f))
                .age(10);

        private static final ClientParticleBuilder petalParticleFactory = new ClientParticleBuilder(BMDParticles.PETAL.get())
                .color(f -> MathUtils.lerpVec(f, Vec3Colors.PINK, Vec3Colors.ULTRA_DARK_PURPLE))
                .brightness(BMDParticles.FULL_BRIGHT)
                .colorVariation(0.15)
                .scale(f -> 0.15f * (1 - f * 0.25f))
                .age(30);
    }
}