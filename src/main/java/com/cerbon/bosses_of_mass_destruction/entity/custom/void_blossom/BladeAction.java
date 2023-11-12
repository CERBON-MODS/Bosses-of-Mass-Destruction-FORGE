package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventSeries;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.HitboxId;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.NetworkedHitboxManager;
import com.cerbon.bosses_of_mass_destruction.entity.util.ProjectileThrower;
import com.cerbon.bosses_of_mass_destruction.projectile.PetalBladeProjectile;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;

public class BladeAction implements IActionWithCooldown {
    private final VoidBlossomEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public BladeAction(VoidBlossomEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel){
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayer)) return 80;

        eventScheduler.addEvent(
                new EventSeries(
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.Petal.getId()),
                                10,
                                1,
                                shouldCancel
                        ),
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.Idle.getId()),
                                90
                        )
                )
        );

        Runnable thrower = () -> {
            Vec3 eyePos = target.getBoundingBox().getCenter();
            Vec3 dir = entity.getEyePosition().subtract(eyePos);
            Vec3 left = dir.cross(VecUtils.yAxis).normalize();
            double rotation = RandomUtils.randSign() * 20.0;
            Vec3 angled = VecUtils.rotateVector(left, dir, rotation);
            Vec3 lineStart = eyePos.add(angled.multiply(7.0, 7.0, 7.0));
            Vec3 lineEnd = eyePos.add(angled.multiply(-7.0, -7.0, -7.0));
            ProjectileThrower projectileThrower = new ProjectileThrower(
                    () -> {
                        PetalBladeProjectile projectile = new PetalBladeProjectile(entity, entity.level(), livingEntity -> {}, List.of(entity.getType()), (float) rotation);
                        projectile.setPos(entity.getEyePosition().add(VecUtils.yAxis));
                        projectile.setNoGravity(true);
                        return new ProjectileThrower.ProjectileData(projectile, 0.9f, 0f, 0.0);
                    }
            );

            BMDUtils.playSound(((ServerPlayer) target).serverLevel(), entity.position(), BMDSounds.PETAL_BLADE.get(), SoundSource.HOSTILE, 3.0f, BMDUtils.randomPitch(target.getRandom()), 64.0, null);
            MathUtils.lineCallback(lineStart, lineEnd, 11, (vec3, integer) -> projectileThrower.throwProjectile(vec3));
        };

        eventScheduler.addEvent(new TimedEvent(thrower, 28, 1, shouldCancel));
        eventScheduler.addEvent(new TimedEvent(thrower, 52, 1, shouldCancel));
        eventScheduler.addEvent(new TimedEvent(thrower, 75, 1, shouldCancel));

        return 120;
    }
}
