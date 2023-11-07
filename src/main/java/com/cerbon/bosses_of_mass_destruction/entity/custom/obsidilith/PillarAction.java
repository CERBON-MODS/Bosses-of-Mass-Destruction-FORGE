package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

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
        this.eventScheduler = BMDCapabilities.getLevelEventScheduler(entity.level());
    }

    @Override
    public int perform() {
        Level level = entity.level();
        if (!(level instanceof ServerLevel)) return 80;
        List<BlockPos> pillarPositions = getPillarPositions();
        BMDUtils.playSound((ServerLevel) level, entity.position(), BMDSounds.OBSIDILITH_PREPARE_ATTACK.get(), SoundSource.HOSTILE, 3.0f, 1.4f, 64, null);

        pillarPositions.forEach(blockPos -> {
            MathUtils.lineCallback(MobUtils.eyePos(entity), VecUtils.asVec3(blockPos).add(0.5, 0.5, 0.5), (int) pillarXzDistance, (vec3, integer) -> BMDUtils.spawnParticle((ServerLevel) level, BMDParticles.PILLAR_SPAWN_INDICATOR_2.get(), vec3, Vec3.ZERO, 0, 0.0));
            BMDUtils.spawnParticle((ServerLevel) level, BMDParticles.PILLAR_SPAWN_INDICATOR.get(), VecUtils.asVec3(blockPos.above()), new Vec3(0.3, 3.0, 0.3), 20, 0.0);
        });

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> pillarPositions.forEach(
                                blockPos -> buildPillar(blockPos, (ServerLevel) level)
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
            Vec3 position = VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().multiply(pillarXzDistance, pillarXzDistance, pillarXzDistance).add(entity.position());
            BlockPos above = BMDUtils.findGroundBelow(entity.level(), BlockPos.containing(position).above(14), pos -> true);
            BlockPos ground = BMDUtils.findGroundBelow(entity.level(), above, pos -> true);

            if (above.getY() - ground.getY() > maxYDistance) continue;

            pillars.add(ground);
        }

        return pillars;
    }

    private void buildPillar(BlockPos pos, ServerLevel serverLevel){
        int pillarHeight = 2;
        IntStream.range(0, pillarHeight).forEach(i -> entity.level().setBlockAndUpdate(pos.above(i), Blocks.OBSIDIAN.defaultBlockState()));
        BlockPos pillarTop = pos.above(pillarHeight);
        entity.level().setBlockAndUpdate(pillarTop, Blocks.OAK_PLANKS.defaultBlockState()); //TODO: Change to OBSIDILITH_RUNE block
        BMDUtils.playSound(serverLevel, VecUtils.asVec3(pos), SoundEvents.BASALT_PLACE, SoundSource.HOSTILE, 1.0f, 16.0, null);
    }
}
