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
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class CometRageAction implements IActionWithCooldown {
    private final LichEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;
    private final Function<Vector3d, ProjectileThrower> cometThrower;

    public static final int numCometsDuringRage = 6;
    public static final int initialRageCometDelay = 60;
    public static final int delayBetweenRageComets = 30;
    public static final int rageCometsMoveDuration = initialRageCometDelay + (numCometsDuringRage * delayBetweenRageComets);

    public CometRageAction(LichEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel, LichConfig lichConfig) {
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
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayerEntity)) return rageCometsMoveDuration;
        performCometThrow((ServerPlayerEntity) target);
        return rageCometsMoveDuration;
    }

    private void performCometThrow(ServerPlayerEntity target) {
        List<Vector3d> offsets = getRageCometOffsets(entity);

        for (int i = 0; i < offsets.size(); i++) {
            Vector3d offset = offsets.get(i);

            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                Vector3d targetPos = target.getBoundingBox().getCenter();
                                cometThrower.apply(offset).throwProjectile(targetPos);
                                BMDUtils.playSound(
                                        target.getLevel(),
                                        entity.position(),
                                        BMDSounds.COMET_SHOOT.get(),
                                        SoundCategory.HOSTILE,
                                        3.0f,
                                        64,
                                        null
                                );
                            },
                            initialRageCometDelay + (i * delayBetweenRageComets),
                            1,
                            shouldCancel
                    )
            );
        }

        BMDUtils.playSound(
                target.getLevel(),
                entity.position(),
                BMDSounds.RAGE_PREPARE.get(),
                SoundCategory.HOSTILE,
                1.0f,
                64,
                null
        );
    }

    public static List<Vector3d> getRageCometOffsets(LichEntity entity) {
        List<Vector3d> offsets = new ArrayList<>();
        MathUtils.circleCallback(3.0, numCometsDuringRage, entity.getLookAngle(), offsets::add);
        return offsets;
    }
}
