package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.static_utilities.ParticleUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Spikes {
    private final LivingEntity entity;
    private final ServerLevel level;
    private final ParticleOptions indicatorParticle;
    private final int riftTime;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public Spikes(LivingEntity entity, ServerLevel level, ParticleOptions indicatorParticle, int riftTime, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.level = level;
        this.indicatorParticle = indicatorParticle;
        this.riftTime = riftTime;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    public List<BlockPos> tryPlaceRift(Vec3 pos) {
        List<BlockPos> blockPosList = new ArrayList<>();
        for (int i = 0; i <= 12; i += 6) {
            BlockPos above = BlockPos.containing(pos.add(VecUtils.yAxis.scale(i)));
            BlockPos groundPos = BMDUtils.findGroundBelow(level, above, this::isOpenBlock);
            BlockPos up = groundPos.above();
            if (up.getY() + 5 >= above.getY()) {
                placeRift(up);
                blockPosList.add(groundPos);
            }
        }
        return blockPosList;
    }


    private void placeRift(BlockPos pos){
        ParticleUtils.spawnParticle(
                level,
                indicatorParticle,
                VecUtils.asVec3(pos).add(new Vec3(0.5, 0.1, 0.5)),
                Vec3.ZERO,
                0,
                0.0
        );

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            AABB box = new AABB(pos).inflate(.0, 4.0, .0);
                            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, livingEntity -> livingEntity != entity);

                            entities.forEach(livingEntity -> {
                                if (livingEntity != entity)
                                    damageEntity(livingEntity);
                            });
                        },
                        riftTime,
                        1,
                        shouldCancel
                )
        );
    }

    private void damageEntity(LivingEntity livingEntity){
        float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        livingEntity.hurt(BMDUtils.shieldPiercing(level, entity), damage);
    }

    private boolean isOpenBlock(BlockPos up){
        return level.getBlockState(up).canBeReplaced(
                new DirectionalPlaceContext(
                        level,
                        up,
                        Direction.DOWN,
                        ItemStack.EMPTY,
                        Direction.UP
                )
        ) || level.getBlockState(up).getBlock() == Blocks.MOSS_CARPET;
    }
}
