package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.EntityAdapter;
import com.cerbon.bosses_of_mass_destruction.entity.util.EntityStats;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.HealS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class VoidBlossomBlock extends Block {
    private static final int healAnimationDelay = 16;
    private static final int healDelay = 64;
    private final VoxelShape shape = box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

    public VoidBlossomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(@Nonnull BlockState state, World level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean movedByPiston) {
        level.getBlockTicks().scheduleTick(pos, this, 1);
    }

    @Override
    public void tick(@Nonnull BlockState state, ServerWorld level, @Nonnull BlockPos pos, @Nonnull Random random) {
        level.getBlockTicks().scheduleTick(pos, this, healDelay);
        healNearbyEntities(level, pos);
    }

    private void healNearbyEntities(ServerWorld level, BlockPos pos){
        level.getEntitiesOfClass(VoidBlossomEntity.class, new AxisAlignedBB(pos).inflate(40.0, 20.0, 40.0))
                .forEach(voidBlossom -> {
                    BMDCapabilities.getLevelEventScheduler(level).addEvent(
                            new TimedEvent(
                                    () -> LichUtils.cappedHeal(new EntityAdapter(voidBlossom), new EntityStats(voidBlossom), VoidBlossomEntity.hpMilestones, 10f, voidBlossom::heal),
                                    healAnimationDelay
                            ));

                    BMDPacketHandler.sendToAllPlayersTrackingChunk(new HealS2CPacket(VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5)), voidBlossom.position().add(VecUtils.yAxis.scale(5.0))), level, voidBlossom.position());
                });
    }

    @Override
    public @Nonnull BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighborState, @Nonnull IWorld level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)){
            destroy(level, pos, state);
            return Blocks.AIR.defaultBlockState();
        }else
            return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(@Nonnull BlockState state, @Nonnull IWorldReader level, @Nonnull BlockPos pos) {
        return canSupportCenter(level, pos, Direction.UP) && !level.isWaterAt(pos);
    }

    @Override
    public void destroy(@Nonnull IWorld level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        for(int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for(int y = -1; y <= 1; y ++) {
                    if((x != 0 || z != 0)) {
                        BlockPos pos1 = pos.offset(x, y, z);
                        if(level.getBlockState(pos1).getBlock() == BMDBlocks.VINE_WALL.get()) {
                            level.getBlockTicks().scheduleTick(pos1, BMDBlocks.VINE_WALL.get(), (2 - y) * 20 + RandomUtils.range(0, 19));
                        }
                    }
                }
            }
        }
    }

    @Override
    public @Nonnull VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader level, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return shape;
    }

    @Override
    public void playerWillDestroy(World level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity player) {
        if (level.isClientSide) {
            for (int i = 0; i < 12; i++){
                Vector3d vel = VecUtils.yAxis.scale(RandomUtils.range(0.1, 0.2));
                Vector3d spawnPos = VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5)).add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(0.5));
                Particles.spikeParticleFactory.build(spawnPos, vel);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleVoidBlossomHeal(ClientWorld level, Vector3d source, Vector3d dest){
        spawnHealParticle(dest, source, level);
        spawnChargeParticle(source, level);
    }

    @OnlyIn(Dist.CLIENT)
    private static void spawnChargeParticle(Vector3d source, ClientWorld level){
        Vector3d particlePos = source.add(VecUtils.yAxis.scale(0.25));
        BMDCapabilities.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> Particles.healParticleFactory.build(particlePos.add(RandomUtils.randVec().scale(0.2)), Vector3d.ZERO),
                        32,
                        32,
                        () -> false
                )
        );
    }

    @OnlyIn(Dist.CLIENT)
    private static void spawnHealParticle(Vector3d dest, Vector3d source, ClientWorld level){
        List<Vector3d> particlePositions = new ArrayList<>();
        int numCirclePoints = healAnimationDelay;
        List<Vector3d> circlePoints = new ArrayList<>(MathUtils.circlePoints(0.5, numCirclePoints, dest.subtract(source).normalize()));
        MathUtils.lineCallback(source, dest, 32, (pos, i) ->
                particlePositions.add(pos.add(circlePoints.get(i % numCirclePoints))));

        AtomicInteger i = new AtomicInteger();
        BMDCapabilities.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> {
                            Particles.healParticleFactory.build(particlePositions.get(i.get()), Vector3d.ZERO);
                            Particles.healParticleFactory.build(particlePositions.get(i.get() + 1), Vector3d.ZERO);
                            i.addAndGet(2);
                        },
                        0,
                        healAnimationDelay,
                        () -> false
                )
        );
    }

    public static void handleVoidBlossomPlace(Vector3d pos){
        for (int i = 0; i <= 12; i++){
            Vector3d spawnPos = pos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(0.5));
            Vector3d vel = VecUtils.yAxis.scale(RandomUtils.range(0.1, 0.3));
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
                .color(f -> MathUtils.lerpVec(f, BMDColors.VOID_PURPLE, BMDColors.ULTRA_DARK_PURPLE))
                .colorVariation(0.15)
                .brightness(BMDParticles.FULL_BRIGHT)
                .scale(0.25f)
                .age(10, 15);

        private static final ClientParticleBuilder healParticleFactory = new ClientParticleBuilder(BMDParticles.OBSIDILITH_BURST.get())
                .color(f -> MathUtils.lerpVec(f, BMDColors.PINK, BMDColors.ULTRA_DARK_PURPLE))
                .colorVariation(0.15)
                .brightness(BMDParticles.FULL_BRIGHT)
                .scale(f -> 0.4f * (1 - f * 0.75f))
                .age(10);

        private static final ClientParticleBuilder petalParticleFactory = new ClientParticleBuilder(BMDParticles.PETAL.get())
                .color(f -> MathUtils.lerpVec(f, BMDColors.PINK, BMDColors.ULTRA_DARK_PURPLE))
                .brightness(BMDParticles.FULL_BRIGHT)
                .colorVariation(0.15)
                .scale(f -> 0.15f * (1 - f * 0.25f))
                .age(30);
    }
}
