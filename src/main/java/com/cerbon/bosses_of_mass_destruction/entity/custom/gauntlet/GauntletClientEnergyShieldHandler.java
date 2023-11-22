package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.Event;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IDataAccessorHandler;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.phys.Vec3;

public class GauntletClientEnergyShieldHandler implements IDataAccessorHandler {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;

    private float energizedRenderAlpha = 0.0f;
    private final ClientParticleBuilder energizedParticles = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
            .brightness(BMDParticles.FULL_BRIGHT)
            .color(BMDColors.LASER_RED)
            .colorVariation(0.2)
            .scale(0.25f);

    public GauntletClientEnergyShieldHandler(GauntletEntity entity, EventScheduler eventScheduler) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
    }

    public float getRenderAlpha(){
        return energizedRenderAlpha;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (GauntletEntity.isEnergized == data && entity.getEntityData().get(GauntletEntity.isEnergized) && entity.level().isClientSide()){
            eventScheduler.addEvent(new TimedEvent(() -> energizedRenderAlpha += 0.1f, 0, 10, () -> false));
            eventScheduler.addEvent(
                    new Event(
                            () -> true,
                            this::spawnParticles,
                            () -> !entity.isAlive() || !entity.getEntityData().get(GauntletEntity.isEnergized)
                    )
            );
        } else
            energizedRenderAlpha = 0.0f;
    }

    private void spawnParticles(){
        Vec3 look = entity.getLookAngle();
        Vec3 cross = look.cross(VecUtils.yAxis);
        Vec3 rotatedOffset = VecUtils.rotateVector(cross, look, RandomUtils.range(0, 359));
        Vec3 particlePos = MobUtils.eyePos(entity).add(rotatedOffset);
        Vec3 particleVel = VecUtils.rotateVector(rotatedOffset, look, 90).scale(0.1);
        energizedParticles.build(particlePos, particleVel);
    }

    public void initDataTracker(){
        entity.getEntityData().define(GauntletEntity.isEnergized, false);
    }
}
