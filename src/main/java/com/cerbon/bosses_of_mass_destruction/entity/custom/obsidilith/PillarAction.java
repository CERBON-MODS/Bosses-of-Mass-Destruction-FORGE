package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PillarAction implements IActionWithCooldown {
    private final LivingEntity entity;
    private final EventScheduler eventScheduler;

    public static final double pillarXzDistance = 13.0;
    public static final double maxYDistance = 15.0;
    public static final int pillarDelay = 40;

    public PillarAction(LivingEntity entity){
        this.entity = entity;
        this.eventScheduler = BMDCapabilities.getLevelEventScheduler(entity.level);
    }

    @Override
    public int perform() {
        World level = entity.level;
        if (!(level instanceof ServerWorld)) return 80;
        List<BlockPos> pillarPositions = getPillarPositions();
        BMDUtils.playSound((ServerWorld) level, entity.position(), BMDSounds.OBSIDILITH_PREPARE_ATTACK.get(), SoundCategory.HOSTILE, 3.0f, 1.4f, 64, null);

        pillarPositions.forEach(blockPos -> {
            MathUtils.lineCallback(MobUtils.eyePos(entity), VecUtils.asVec3(blockPos).add(0.5, 0.5, 0.5), (int) pillarXzDistance, (vec3, integer) -> BMDUtils.spawnParticle((ServerWorld) level, BMDParticles.PILLAR_SPAWN_INDICATOR_2.get(), vec3, Vector3d.ZERO, 0, 0.0));
            BMDUtils.spawnParticle((ServerWorld) level, BMDParticles.PILLAR_SPAWN_INDICATOR.get(), VecUtils.asVec3(blockPos.above(5)), new Vector3d(0.3, 3.0, 0.3), 20, 0.0);
        });

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> pillarPositions.forEach(
                                blockPos -> buildPillar(blockPos, (ServerWorld) level)
                        ),
                        pillarDelay,
                        1,
                        () -> !entity.isAlive()
                )
        );
        return 100;
    }

    private List<BlockPos> getPillarPositions(){
        int numPillars = 4;
        List<BlockPos> pillars = new ArrayList<>();

        for (int i = 0; i < numPillars; i++){
            Vector3d position = VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(pillarXzDistance).add(entity.position());
            BlockPos above = BMDUtils.findGroundBelow(entity.level, new BlockPos(position).above(14), pos -> true);
            BlockPos ground = BMDUtils.findGroundBelow(entity.level, above, pos -> true);

            if (above.getY() - ground.getY() > maxYDistance) continue;

            pillars.add(ground);
        }

        return pillars;
    }

    private void buildPillar(BlockPos pos, ServerWorld serverLevel){
        int pillarHeight = 2;
        IntStream.range(0, pillarHeight).forEach(i -> entity.level.setBlockAndUpdate(pos.above(i), Blocks.OBSIDIAN.defaultBlockState()));
        BlockPos pillarTop = pos.above(pillarHeight);
        entity.level.setBlockAndUpdate(pillarTop, BMDBlocks.OBSIDILITH_RUNE.get().defaultBlockState());
        BMDUtils.playSound(serverLevel, VecUtils.asVec3(pos), SoundEvents.BASALT_PLACE, SoundCategory.HOSTILE, 1.0f, 16.0, null);
    }
}
