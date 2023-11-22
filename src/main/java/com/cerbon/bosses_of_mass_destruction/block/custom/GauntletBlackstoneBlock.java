package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GauntletBlackstoneBlock extends Block {

    public GauntletBlackstoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (level.isClientSide) return;

        for (Direction dir : List.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)){
            BlockPos centerPos = pos.offset(dir.getNormal());
            Block centerBlock = level.getBlockState(centerPos).getBlock();

            if (centerBlock == this){
                spawnGauntlet(centerPos, level);
                break;
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    private void spawnGauntlet(BlockPos centerPos, Level level){
        Vec3 spawnPos = VecUtils.asVec3(centerPos).add(new Vec3(0.5, -0.5, 0.5));
        GauntletEntity entity = BMDEntities.GAUNTLET.get().create(level);

        if (entity != null){
            entity.absMoveTo(spawnPos.x, spawnPos.y, spawnPos.z);
            level.addFreshEntity(entity);
        }

        EventScheduler eventScheduler = BMDCapabilities.getLevelEventScheduler(level);
        for (int y = -1; y <= 4; y++){
            int y1 = y;
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                for (int x = -1; x <= 1; x++)
                                    for (int z = -1; z <= 1; z++)
                                        level.destroyBlock(centerPos.offset(x, y1, z), false);
                            },
                            10 + y * 5
                    )
            );
        }
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        Vec3 particlePos = VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5)).add(RandomUtils.randVec().normalize());
        Particles.laserChargeParticles.build(particlePos,Vec3.ZERO);
    }

    public static class Particles {
        public static final ClientParticleBuilder laserChargeParticles = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
                .brightness(BMDParticles.FULL_BRIGHT)
                .color(BMDColors.LASER_RED)
                .colorVariation(0.2);
    }
}
