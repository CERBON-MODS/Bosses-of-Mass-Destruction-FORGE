package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.ModRandom;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.spawn.ISpawnPredicate;
import com.cerbon.bosses_of_mass_destruction.entity.spawn.MobEntitySpawnPredicate;
import com.cerbon.bosses_of_mass_destruction.entity.spawn.MobPlacementLogic;
import com.cerbon.bosses_of_mass_destruction.entity.spawn.RangedSpawnPosition;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Supplier;

public class TeleportAction implements IActionWithCooldown {
    private final LichEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public static final double tooFarFromTargetDistance = 35.0;
    public static final double tooCloseToTargetDistance = 20.0;
    public static final int teleportCooldown = 80;
    public static final int teleportStartSoundDelay = 10;
    public static final int teleportDelay = 40;
    public static final int beginTeleportParticleDelay = 15;
    public static final int teleportParticleDuration = 10;

    public TeleportAction(LichEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayerEntity)) return teleportCooldown;
        performTeleport((ServerPlayerEntity) target);
        return teleportCooldown;
    }

    public void performTeleport(ServerPlayerEntity target) {
        MobEntitySpawnPredicate spawnPredicate = new MobEntitySpawnPredicate(target.level);
        ISpawnPredicate entitySpawnPredicate = (pos, e) -> spawnPredicate.canSpawn(pos, e) && entity.inLineOfSight(target);
        teleport(target, entitySpawnPredicate, spawnPredicate);
    }

    private void teleport(ServerPlayerEntity target, ISpawnPredicate spawnPredicate, ISpawnPredicate backupPredicate) {
        MobPlacementLogic mobPlacementLogic = buildTeleportLogic(target, target.position(), spawnPredicate);
        boolean success = mobPlacementLogic.tryPlacement(100);
        if (!success) {
            Vector3d safePos = VecUtils.asVec3(entity.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(target.position())));
            buildTeleportLogic(target, safePos, backupPredicate).tryPlacement(100);
        }
    }

    private MobPlacementLogic buildTeleportLogic(ServerPlayerEntity target, Vector3d spawnPos, ISpawnPredicate spawnPredicate) {
        return new MobPlacementLogic(
                new RangedSpawnPosition(spawnPos, tooCloseToTargetDistance, tooFarFromTargetDistance, new ModRandom()),
                () -> entity,
                spawnPredicate,
                (pos, e) -> eventScheduler.addEvent(
                        new TimedEvent(
                                () -> {
                                    BMDUtils.playSound(target.getLevel(), entity.position(), BMDSounds.TELEPORT_PREPARE.get(), SoundCategory.HOSTILE, 3.0f, 64, null);
                                    entity.collides = false;
                                    eventScheduler.addEvent(
                                            new TimedEvent(
                                                    () -> {
                                                        e.teleportTo(pos.x, pos.y, pos.z);
                                                        e.level.broadcastEntityEvent(e, LichActions.endTeleport);
                                                        BMDUtils.playSound(target.getLevel(), entity.position(), BMDSounds.LICH_TELEPORT.get(), SoundCategory.HOSTILE, 2.0f, 64, null);
                                                        entity.collides = true;
                                                        },
                                                    teleportDelay - teleportStartSoundDelay
                                            )
                                    );
                                },
                                teleportStartSoundDelay,
                                1,
                                shouldCancel
                        )
                )
        );
    }
}

