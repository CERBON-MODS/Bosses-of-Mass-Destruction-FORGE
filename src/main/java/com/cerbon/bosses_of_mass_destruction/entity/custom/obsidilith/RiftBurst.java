package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class RiftBurst {
    private final LivingEntity entity;
    private final ServerWorld serverLevel;
    private final IParticleData indicatorParticle;
    private final IParticleData columnParticle;
    private final int riftTime;
    private final EventScheduler eventScheduler;
    private final Consumer<LivingEntity> onImpact;
    private final Function<BlockPos, Boolean> isOpenBlock;
    private final Function<Vector3d, BlockPos> posFinder;

    public RiftBurst(LivingEntity entity, ServerWorld serverLevel, IParticleData indicatorParticle, IParticleData columnParticle, int riftTime, EventScheduler eventScheduler, Consumer<LivingEntity> onImpact, Function<BlockPos, Boolean> isOpenBlock, Function<Vector3d, BlockPos> posFinder){
        this.entity = entity;
        this.serverLevel = serverLevel;
        this.indicatorParticle = indicatorParticle;
        this.columnParticle = columnParticle;
        this.riftTime = riftTime;
        this.eventScheduler = eventScheduler;
        this.onImpact = onImpact;
        this.isOpenBlock = isOpenBlock;
        this.posFinder = posFinder;
    }

    public RiftBurst(LivingEntity entity, ServerWorld serverLevel, IParticleData indicatorParticle, IParticleData columnParticle, int riftTime, EventScheduler eventScheduler, Consumer<LivingEntity> onImpact){
        this.entity = entity;
        this.serverLevel = serverLevel;
        this.indicatorParticle = indicatorParticle;
        this.columnParticle = columnParticle;
        this.riftTime = riftTime;
        this.eventScheduler = eventScheduler;
        this.onImpact = onImpact;
        this.isOpenBlock = isOpenBlock(serverLevel);
        this.posFinder = defaultPosFinder(serverLevel, isOpenBlock);
    }

    public void tryPlaceRift(Vector3d pos){
        BlockPos placement = posFinder.apply(pos);
        if (placement != null)
            placeRift(placement);
    }

    private void placeRift(BlockPos pos){
        BMDUtils.spawnParticle(
                serverLevel,
                indicatorParticle,
                VecUtils.asVec3(pos).add(new Vector3d(0.5, 0.1, 0.5)),
                Vector3d.ZERO,
                0,
                0.0
        );

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            Random rand = serverLevel.random;
                            Vector3d columVel = VecUtils.yAxis.scale(rand.nextDouble() + 1).scale(0.25);
                            AtomicInteger ticks = new AtomicInteger();
                            eventScheduler.addEvent(
                                    new TimedEvent(
                                            () -> {
                                                BlockPos impactPos = pos.above(ticks.get());
                                                BMDUtils.spawnParticle(
                                                        serverLevel,
                                                        columnParticle,
                                                        VecUtils.asVec3(impactPos).add(VecUtils.unit.scale(0.5)).add(RandomUtils.randVec().scale(0.25)),
                                                        columVel,
                                                        0,
                                                        0.0
                                                );

                                                AxisAlignedBB aabb = new AxisAlignedBB(impactPos);
                                                List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, aabb, entity1 -> entity1 != entity);

                                                entities.forEach(entity1 -> {
                                                    if (entity1 != entity)
                                                        onImpact.accept(entity1);
                                                });

                                                ticks.addAndGet(2);
                                            },
                                            0,
                                            7,
                                            () -> false
                                    )
                            );
                        },
                        riftTime,
                        1,
                        () -> !isOpenBlock.apply(pos) || !entity.isAlive()
                )
        );
    }

    private Function<Vector3d, BlockPos> defaultPosFinder(ServerWorld serverLevel, Function<BlockPos, Boolean> isOpenBlock) {
        return vec3 -> {
            BlockPos above = new BlockPos(vec3.add(VecUtils.yAxis.scale(14.0)));
            BlockPos groundPos = BMDUtils.findGroundBelow(serverLevel, above, pos -> true);
            BlockPos up = groundPos.above();
            return (up.getY() + 28 >= above.getY() && isOpenBlock.apply(up)) ? up : null;
        };
    }

    private Function<BlockPos, Boolean> isOpenBlock(ServerWorld serverLevel){
        return blockPos -> serverLevel.getBlockState(blockPos).canBeReplaced(
                new DirectionalPlaceContext(
                        serverLevel,
                        blockPos,
                        Direction.DOWN,
                        ItemStack.EMPTY,
                        Direction.UP
                )
        );
    }
}

