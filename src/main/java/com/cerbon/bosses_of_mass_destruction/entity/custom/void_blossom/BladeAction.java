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
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;

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
        if (!(target instanceof ServerPlayerEntity)) return 80;

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
            Vector3d eyePos = target.getBoundingBox().getCenter();
            Vector3d dir = entity.getEyePosition(1.0F).subtract(eyePos);
            Vector3d left = dir.cross(VecUtils.yAxis).normalize();
            double rotation = RandomUtils.randSign() * 20.0;
            Vector3d angled = VecUtils.rotateVector(left, dir, rotation);
            Vector3d lineStart = eyePos.add(angled.scale(7.0));
            Vector3d lineEnd = eyePos.add(angled.scale(-7.0));
            ProjectileThrower projectileThrower = new ProjectileThrower(
                    () -> {
                        PetalBladeProjectile projectile = new PetalBladeProjectile(entity, entity.level, livingEntity -> {}, Lists.newArrayList(entity.getType()), (float) rotation);
                        Vector3d pos = entity.getEyePosition(1.0f).add(VecUtils.yAxis);
                        projectile.setPos(pos.x, pos.y, pos.z);
                        projectile.setNoGravity(true);
                        return new ProjectileThrower.ProjectileData(projectile, 0.9f, 0f, 0.0);
                    }
            );

            BMDUtils.playSound(((ServerPlayerEntity) target).getLevel(), entity.position(), BMDSounds.PETAL_BLADE.get(), SoundCategory.HOSTILE, 3.0f, BMDUtils.randomPitch(target.getRandom()), 64.0, null);
            MathUtils.lineCallback(lineStart, lineEnd, 11, (vec3, integer) -> projectileThrower.throwProjectile(vec3));
        };

        eventScheduler.addEvent(new TimedEvent(thrower, 28, 1, shouldCancel));
        eventScheduler.addEvent(new TimedEvent(thrower, 52, 1, shouldCancel));
        eventScheduler.addEvent(new TimedEvent(thrower, 75, 1, shouldCancel));

        return 120;
    }
}
