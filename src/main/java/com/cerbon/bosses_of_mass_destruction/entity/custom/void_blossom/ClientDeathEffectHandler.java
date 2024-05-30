package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.util.math.vector.Vector3d;

public class ClientDeathEffectHandler implements IEntityEventHandler {
    private final VoidBlossomEntity entity;
    private final EventScheduler eventScheduler;

    private final ClientParticleBuilder deathParticle = new ClientParticleBuilder(BMDParticles.FLUFF.get())
            .color(BMDColors.DARK_GREY)
            .colorVariation(0.1)
            .age(20, 30)
            .scale(0.3f);

    public ClientDeathEffectHandler(VoidBlossomEntity entity, EventScheduler eventScheduler) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
    }

    @Override
    public void handleEntityEvent(byte status) {
        if ((int) status == 3){
            int delay = 3;
            Vector3d fallDirection = VecUtils.planeProject(entity.getForward(), VecUtils.yAxis).yRot(180f);
            Vector3d originPos = entity.position().add(VecUtils.yAxis.scale(2.0));
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                Vector3d pos = originPos
                                        .add(RandomUtils.randVec().scale(5.0))
                                        .add(fallDirection.scale(RandomUtils.randomDouble(6.0) + 6.0));
                                Vector3d vel = RandomUtils.randVec().add(VecUtils.yAxis).scale(0.05);
                                deathParticle.build(pos, vel);
                            },
                            delay,
                            (int) LightBlockRemover.deathMaxAge - delay,
                            () -> false
                    )
            );
        }
    }
}
