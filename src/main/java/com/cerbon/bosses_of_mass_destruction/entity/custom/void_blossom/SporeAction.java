package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventSeries;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.HitboxId;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.NetworkedHitboxManager;
import com.cerbon.bosses_of_mass_destruction.entity.util.ProjectileThrower;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.util.ExemptEntities;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Supplier;

public class SporeAction implements IActionWithCooldown {
    private final VoidBlossomEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public SporeAction(VoidBlossomEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
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
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.Spore.getId()),
                                20,
                                1,
                                shouldCancel
                        ),
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.Idle.getId()),
                                27,
                                1,
                                () -> false
                        )
                )
        );

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> BMDUtils.playSound(((ServerPlayer) target).serverLevel(), entity.position(), BMDSounds.SPORE_PREPARE.get(), SoundSource.HOSTILE, 1.5f, 32, null),
                        26
                )
        );

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> new ProjectileThrower(
                                () -> {
                                    SporeBallProjectile projectile = new SporeBallProjectile(entity, entity.level(), new ExemptEntities(List.of(BMDEntities.VOID_BLOSSOM.get())));
                                    projectile.setPos(entity.getEyePosition());
                                    return new ProjectileThrower.ProjectileData(projectile, 0.75f, 0f, 0.2);
                                }
                        ).throwProjectile(target.getEyePosition()),
                        45,
                        1,
                        shouldCancel
                )
        );

        return 100;
    }
}
