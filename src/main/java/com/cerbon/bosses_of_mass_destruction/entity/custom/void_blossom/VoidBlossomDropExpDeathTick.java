package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class VoidBlossomDropExpDeathTick implements IEntityTick<ServerWorld> {
    private final LivingEntity entity;
    private final EventScheduler eventScheduler;
    private final int exp;

    public VoidBlossomDropExpDeathTick(LivingEntity entity, EventScheduler eventScheduler, int exp) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.exp = exp;
    }

    @Override
    public void tick(ServerWorld level) {
        if (entity.deathTime == 1)
            scheduleExp();
    }

    private void scheduleExp(){
        int expTicks = 20;
        int expPerTick = (int) (exp / (float) expTicks);
        Vector3d fallDirection = VecUtils.planeProject(Vector3d.directionFromRotation(entity.getRotationVector()), VecUtils.yAxis).yRot(180f);
        Vector3d originPos = entity.position().add(VecUtils.yAxis.scale(2.0));

        eventScheduler.addEvent(
                new TimedEvent(
                        () ->{
                            Vector3d pos = originPos
                                    .add(RandomUtils.randVec().scale(2.0))
                                    .add(fallDirection.scale(RandomUtils.randomDouble(6.0) + 6.0));

                            VanillaCopiesServer.awardExperience(
                                    expPerTick,
                                    pos,
                                    entity.level
                            );
                        },
                        (int) (LightBlockRemover.deathMaxAge - expTicks - 1),
                        expTicks,
                        () -> false
                )
        );
    }
}
