package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.static_utilities.ParticleUtils;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class RiftBurst {
    private final LivingEntity entity;
    private final ServerLevel serverLevel;
    private final ParticleOptions indicatorParticle;
    private final ParticleOptions columnParticle;
    private final int riftTime;
    private final EventScheduler eventScheduler;
    private final Consumer<LivingEntity> onImpact;
    private final Function<BlockPos, Boolean> isOpenBlock;
    private final Function<Vec3, BlockPos> posFinder;

    public RiftBurst(LivingEntity entity, ServerLevel serverLevel, ParticleOptions indicatorParticle, ParticleOptions columnParticle, int riftTime, EventScheduler eventScheduler, Consumer<LivingEntity> onImpact, Function<BlockPos, Boolean> isOpenBlock, Function<Vec3, BlockPos> posFinder){
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

    public RiftBurst(LivingEntity entity, ServerLevel serverLevel, ParticleOptions indicatorParticle, ParticleOptions columnParticle, int riftTime, EventScheduler eventScheduler, Consumer<LivingEntity> onImpact){
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

    public void tryPlaceRift(Vec3 pos){
        BlockPos placement = posFinder.apply(pos);
        if (placement != null)
            placeRift(placement);
    }

    private void placeRift(BlockPos pos){
        ParticleUtils.spawnParticle(
                serverLevel,
                indicatorParticle,
                VecUtils.asVec3(pos).add(new Vec3(0.5, 0.1, 0.5)),
                Vec3.ZERO,
                0,
                0.0
        );

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            RandomSource rand = serverLevel.random;
                            Vec3 columVel = VecUtils.yAxis.scale(rand.nextDouble() + 1).scale(0.25);
                            AtomicInteger ticks = new AtomicInteger();
                            eventScheduler.addEvent(
                                    new TimedEvent(
                                            () -> {
                                                BlockPos impactPos = pos.above(ticks.get());
                                                ParticleUtils.spawnParticle(
                                                        serverLevel,
                                                        columnParticle,
                                                        VecUtils.asVec3(impactPos).add(VecUtils.unit.scale(0.5)).add(RandomUtils.randVec().scale(0.25)),
                                                        columVel,
                                                        0,
                                                        0.0
                                                );

                                                AABB aabb = new AABB(impactPos);
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

    private Function<Vec3, BlockPos> defaultPosFinder(ServerLevel serverLevel, Function<BlockPos, Boolean> isOpenBlock) {
        return vec3 -> {
            BlockPos above = BlockPos.containing(vec3.add(VecUtils.yAxis.scale(14.0)));
            BlockPos groundPos = BMDUtils.findGroundBelow(serverLevel, above, pos -> true);
            BlockPos up = groundPos.above();
            return (up.getY() + 28 >= above.getY() && isOpenBlock.apply(up)) ? up : null;
        };
    }

    private Function<BlockPos, Boolean> isOpenBlock(ServerLevel serverLevel){
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

