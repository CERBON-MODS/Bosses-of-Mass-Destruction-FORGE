package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.damagesource.UnshieldableDamageSource;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Spikes {
    private final LivingEntity entity;
    private final ServerWorld level;
    private final IParticleData indicatorParticle;
    private final int riftTime;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public Spikes(LivingEntity entity, ServerWorld level, IParticleData indicatorParticle, int riftTime, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.level = level;
        this.indicatorParticle = indicatorParticle;
        this.riftTime = riftTime;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    public List<BlockPos> tryPlaceRift(Vector3d pos) {
        List<BlockPos> blockPosList = new ArrayList<>();
        for (int i = 0; i <= 12; i += 6) {
            BlockPos above = new BlockPos(pos.add(VecUtils.yAxis.scale(i)));
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
        BMDUtils.spawnParticle(
                level,
                indicatorParticle,
                VecUtils.asVec3(pos).add(new Vector3d(0.5, 0.1, 0.5)),
                Vector3d.ZERO,
                0,
                0.0
        );

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            AxisAlignedBB box = new AxisAlignedBB(pos).inflate(.0, 4.0, .0);
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
        livingEntity.hurt(new UnshieldableDamageSource(entity), damage);
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
        ) || level.getBlockState(up).getBlock() == Blocks.GRASS;
    }
}
