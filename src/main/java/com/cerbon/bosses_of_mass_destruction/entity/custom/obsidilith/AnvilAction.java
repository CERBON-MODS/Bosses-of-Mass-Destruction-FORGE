package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.Event;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;

public class AnvilAction implements IActionWithCooldown {
    private final Mob actor;
    private final float explosionPower;
    private final EventScheduler eventScheduler;
    private final List<Vec3> circlePoints;

    public AnvilAction(Mob actor, float explosionPower){
        this.actor = actor;
        this.explosionPower = explosionPower;
        this.eventScheduler = BMDCapabilities.getLevelEventScheduler(actor.level);
        this.circlePoints = MathUtils.buildBlockCircle(2.0);
    }

    @Override
    public int perform() {
        LivingEntity target = actor.getTarget();
        Level level = actor.level;
        if (!(level instanceof ServerLevel)) return 80;
        performAttack(target, (ServerLevel) level);
        return 80;
    }

    private void performAttack(LivingEntity target, ServerLevel level){
        BMDUtils.playSound(level, actor.position(), BMDSounds.OBSIDILITH_PREPARE_ATTACK.get(), SoundSource.HOSTILE, 3.0f, 1.0f, 64.0f, null);

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            Vec3 targetPos = target.position();
                            Vec3 teleportPos = targetPos.add(VecUtils.yAxis.scale(24.0));
                            Vec3 originalPos = actor.position();

                            actor.moveTo(teleportPos.x, teleportPos.y, teleportPos.z, actor.getYRot(), actor.getXRot());
                            BMDUtils.playSound(level, teleportPos, BMDSounds.OBSIDILITH_TELEPORT.get(), SoundSource.HOSTILE, 3.0f, 64, null);

                            for (Vec3 pos : circlePoints){
                                BlockPos particlePos = BMDUtils.findGroundBelow(actor.level, new BlockPos(pos.add(targetPos)).above(3), pos1 -> true).above();
                                if (particlePos.getY() != 0)
                                    BMDUtils.spawnParticle(level, BMDParticles.OBSIDILITH_ANVIL_INDICATOR.get(), VecUtils.asVec3(particlePos).add(new Vec3(0.5, 0.1, 0.5)), Vec3.ZERO, 0, 0.0);
                            }

                            //Added other eventScheduler because without giving it a delay before getting the value of onGround method it was not working
                            eventScheduler.addEvent(
                                    new TimedEvent(
                                            () -> {
                                                Supplier<Boolean> shouldLand = () -> actor.isOnGround() || actor.getY() < 0;
                                                Supplier<Boolean> shouldCancelLand = () -> !actor.isAlive() || shouldLand.get();

                                                eventScheduler.addEvent(
                                                        new Event(
                                                                shouldLand,
                                                                () -> {
                                                                    actor.level.explode(actor, actor.getX(), actor.getY(), actor.getZ(), explosionPower, VanillaCopiesServer.getEntityDestructionType(actor.level));
                                                                    eventScheduler.addEvent(
                                                                            new TimedEvent(
                                                                                    () -> {
                                                                                        actor.moveTo(originalPos.x, originalPos.y, originalPos.z, actor.getYRot(), actor.getXRot());
                                                                                        BMDUtils.playSound(level, actor.position(), BMDSounds.OBSIDILITH_TELEPORT.get(), SoundSource.HOSTILE, 1.0f, 64, null);
                                                                                    },
                                                                                    20,
                                                                                    1,
                                                                                    () -> !actor.isAlive()
                                                                            )
                                                                    );
                                                                },
                                                                shouldCancelLand
                                                        )
                                                );
                                            },
                                            1,
                                            1,
                                            () -> !actor.isAlive()
                                    )
                            );
                        },
                        20,
                        1,
                        () -> !actor.isAlive()
                )
        );
    }
}
