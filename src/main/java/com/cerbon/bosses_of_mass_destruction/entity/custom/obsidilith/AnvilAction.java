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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.function.Supplier;

public class AnvilAction implements IActionWithCooldown {
    private final MobEntity actor;
    private final float explosionPower;
    private final EventScheduler eventScheduler;
    private final List<Vector3d> circlePoints;

    public AnvilAction(MobEntity actor, float explosionPower){
        this.actor = actor;
        this.explosionPower = explosionPower;
        this.eventScheduler = BMDCapabilities.getLevelEventScheduler(actor.level);
        this.circlePoints = MathUtils.buildBlockCircle(2.0);
    }

    @Override
    public int perform() {
        LivingEntity target = actor.getTarget();
        World level = actor.level;
        if (!(level instanceof ServerWorld)) return 80;
        performAttack(target, (ServerWorld) level);
        return 80;
    }

    private void performAttack(LivingEntity target, ServerWorld level){
        BMDUtils.playSound(level, actor.position(), BMDSounds.OBSIDILITH_PREPARE_ATTACK.get(), SoundCategory.HOSTILE, 3.0f, 1.0f, 64.0f, null);

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            Vector3d targetPos = target.position();
                            Vector3d teleportPos = targetPos.add(VecUtils.yAxis.scale(24.0));
                            Vector3d originalPos = actor.position();

                            actor.moveTo(teleportPos.x, teleportPos.y, teleportPos.z, actor.yRot, actor.xRot);
                            BMDUtils.playSound(level, teleportPos, BMDSounds.OBSIDILITH_TELEPORT.get(), SoundCategory.HOSTILE, 3.0f, 64, null);

                            for (Vector3d pos : circlePoints){
                                BlockPos particlePos = BMDUtils.findGroundBelow(actor.level, new BlockPos(pos.add(targetPos)).above(3), pos1 -> true).above();
                                if (particlePos.getY() != 0)
                                    BMDUtils.spawnParticle(level, BMDParticles.OBSIDILITH_ANVIL_INDICATOR.get(), VecUtils.asVec3(particlePos).add(new Vector3d(0.5, 0.1, 0.5)), Vector3d.ZERO, 0, 0.0);
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
                                                                                        actor.moveTo(originalPos.x, originalPos.y, originalPos.z, actor.yRot, actor.xRot);
                                                                                        BMDUtils.playSound(level, actor.position(), BMDSounds.OBSIDILITH_TELEPORT.get(), SoundCategory.HOSTILE, 1.0f, 64, null);
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
