package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.ThrowProjectileAction;
import com.cerbon.bosses_of_mass_destruction.entity.util.ProjectileThrower;
import com.cerbon.bosses_of_mass_destruction.projectile.comet.CometProjectile;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

public class CometAction implements IActionWithCooldown {
    private final LichEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;
    private final Function<Vec3, ProjectileThrower> cometThrower;

    public static final int cometThrowDelay = 60;
    public static final int cometParticleSummonDelay = 15;
    public static final int cometThrowCooldown = 80;

    public CometAction(LichEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel, LichConfig lichConfig) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;

        this.cometThrower = offset -> new ProjectileThrower(
                () -> {
                    CometProjectile projectile = new CometProjectile(entity, entity.level, vec3 ->
                            entity.level.explode(entity, vec3.x, vec3.y, vec3.z, lichConfig.comet.explosionStrength, VanillaCopiesServer.getEntityDestructionType(entity.level)), Collections.singletonList(MinionAction.summonEntityType));

                    MobUtils.setPos(projectile, MobUtils.eyePos(entity).add(offset));
                    return new ProjectileThrower.ProjectileData(projectile, 1.6f, 0f, 0.2);
                }
        );
    }

    @Override
    public int perform() {
        Entity target = entity.getTarget();
        if (!(target instanceof ServerPlayer)) return cometThrowCooldown;
        performCometThrow(((ServerPlayer) target).getLevel());
        return cometThrowCooldown;
    }

    private void performCometThrow(ServerLevel serverLevel) {
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> BMDUtils.playSound(
                                serverLevel,
                                entity.position(),
                                BMDSounds.COMET_PREPARE.get(),
                                SoundSource.HOSTILE,
                                3.0f,
                                64.0,
                                null
                        ),
                        10,
                        1,
                        shouldCancel
                )
        );

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            new ThrowProjectileAction(
                                    entity,
                                    cometThrower.apply(getCometLaunchOffset())).perform();

                            BMDUtils.playSound(
                                    serverLevel,
                                    entity.position(),
                                    BMDSounds.COMET_SHOOT.get(),
                                    SoundSource.HOSTILE,
                                    3.0f,
                                    64.0,
                                    null
                            );
                        },
                        cometThrowDelay,
                        1,
                        shouldCancel
                )
        );
    }

    public static Vec3 getCometLaunchOffset() {
        return VecUtils.yAxis.scale(2.0);
    }
}


