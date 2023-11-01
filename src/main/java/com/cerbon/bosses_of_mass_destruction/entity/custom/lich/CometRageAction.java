package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.util.ProjectileThrower;
import com.cerbon.bosses_of_mass_destruction.projectile.comet.CometProjectile;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class CometRageAction implements IActionWithCooldown {
    private final LichEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;
    private final Function<Vec3, ProjectileThrower> cometThrower;

    public CometRageAction(LichEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel, LichConfig lichConfig) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;

        this.cometThrower = offset -> new ProjectileThrower(() -> {
                    CometProjectile projectile = new CometProjectile(entity, entity.level(), vec3 ->
                            entity.level().explode(entity, vec3.x(), vec3.y(), vec3.z(), lichConfig.comet.explosionStrength, Level.ExplosionInteraction.MOB), Collections.singletonList(MinionAction.summonEntityType)
                    );

                    MobUtils.setPos(projectile, MobUtils.eyePos(entity).add(offset));
                    return new ProjectileThrower.ProjectileData(projectile, 1.6f, 0f, 0.2);
                }
        );

    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayer)) return rageCometsMoveDuration;
        performCometThrow((ServerPlayer) target);
        return rageCometsMoveDuration;
    }

    private void performCometThrow(ServerPlayer target) {
        List<Vec3> offsets = getRageCometOffsets(entity);

        for (int i = 0; i < offsets.size(); i++) {
            Vec3 offset = offsets.get(i);

            eventScheduler.addEvent(new TimedEvent(() -> {
                Vec3 targetPos = target.getBoundingBox().getCenter();
                cometThrower.apply(offset).throwProjectile(targetPos);
                BMDUtils.playSound(
                        target.serverLevel(),
                        entity.position(),
                        BMDSounds.COMET_SHOOT.get(),
                        SoundSource.HOSTILE,
                        3.0f,
                        null
                );
            },
                    initialRageCometDelay + (i * delayBetweenRageComets),
                    1,
                    shouldCancel
            ));
        }

        BMDUtils.playSound(
                target.serverLevel(),
                entity.position(),
                BMDSounds.RAGE_PREPARE.get(),
                SoundSource.HOSTILE,
                1.0f,
                null
        );
    }

    public static final int numCometsDuringRage = 6;
    public static final int initialRageCometDelay = 60;
    public static final int delayBetweenRageComets = 30;
    public static final int rageCometsMoveDuration = initialRageCometDelay + (numCometsDuringRage * delayBetweenRageComets);

    public static List<Vec3> getRageCometOffsets(LichEntity entity) {
        List<Vec3> offsets = new ArrayList<>();
        MathUtils.circleCallback(3.0, numCometsDuringRage, entity.getLookAngle(), offsets::add);
        return offsets;
    }
}
