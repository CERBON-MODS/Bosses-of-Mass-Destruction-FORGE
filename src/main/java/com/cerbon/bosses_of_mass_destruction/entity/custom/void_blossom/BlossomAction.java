package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventSeries;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.HitboxId;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.NetworkedHitboxManager;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlossomAction implements IActionWithCooldown {
    private final VoidBlossomEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    private final List<Vec3> blossomPositions = Stream.of(
            VecUtils.xAxis,
            VecUtils.zAxis,
            VecUtils.xAxis.reverse(),
            VecUtils.zAxis.reverse(),
            VecUtils.xAxis.add(VecUtils.zAxis),
            VecUtils.xAxis.add(VecUtils.zAxis.reverse()),
            VecUtils.xAxis.reverse().add(VecUtils.zAxis),
            VecUtils.xAxis.reverse().add(VecUtils.zAxis.reverse())
    ).map(vec3 -> vec3.normalize().multiply(15.0, 15.0, 15.0)).toList();

    public BlossomAction(VoidBlossomEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public int perform() {
        Level level = entity.level();
        if (!(level instanceof ServerLevel)) return 80;

        eventScheduler.addEvent(
                new EventSeries(
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.SpikeWave3.getId()),
                                20,
                                1,
                                shouldCancel
                        ),
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.Idle.getId()),
                                80
                        )
                )
        );
        placeBlossoms((ServerLevel) level);
        return 120;
    }

    private void placeBlossoms(ServerLevel level){
        List<BlockPos> positions = blossomPositions.stream()
                .map(pos -> BlockPos.containing(pos.add(entity.position())))
                .collect(Collectors.toList());
        Collections.shuffle(positions);

        float hpRatio = entity.getHealth() / entity.getMaxHealth();
        int protectedPositions;
        if (hpRatio < VoidBlossomEntity.hpMilestones.get(1))
            protectedPositions = 6;
        else if (hpRatio < VoidBlossomEntity.hpMilestones.get(2))
            protectedPositions = 3;
        else {
            protectedPositions = 0;
        }

        BMDUtils.playSound(level, entity.position(), BMDSounds.SPIKE_WAVE_INDICATOR.get(), SoundSource.HOSTILE, 2.0f, 0.7f, 64.0, null);

        for (int i = 0; i < 8; i++){
            int i1 = i;
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                BlockPos blossomPos = positions.get(i1);
                                level.setBlockAndUpdate(blossomPos, Blocks.MOSS_BLOCK.defaultBlockState());
                                level.setBlockAndUpdate(blossomPos.above(), Blocks.ACACIA_PLANKS.defaultBlockState()); //TODO: Change to VoidBlossom block
                                //TODO: Add Packet
                                BMDUtils.playSound(level, VecUtils.asVec3(blossomPos), BMDSounds.PETAL_BLADE.get(), SoundSource.HOSTILE, 1.0f, BMDUtils.randomPitch(entity.getRandom()), 64, null);

                                if(i1 < protectedPositions) {
                                    for (int x = -1; x <= 1; x++) {
                                        for (int z = -1; z <= 1; z++) {
                                            for (int y = 0; y <= 2; y++) {
                                                if ((x != 0 || z != 0)) {
                                                    level.setBlockAndUpdate(blossomPos.offset(x, y, z), Blocks.ACACIA_LOG.defaultBlockState()); //TODO: Change to vineWall block
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            40 + i * 8,
                            1,
                            shouldCancel
                    )
            );
        }

    }
}
