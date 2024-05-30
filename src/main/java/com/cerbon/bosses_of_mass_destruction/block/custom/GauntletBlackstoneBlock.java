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
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class GauntletBlackstoneBlock extends Block {

    public GauntletBlackstoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void playerWillDestroy(World level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity player) {
        if (level.isClientSide) return;

        for (Direction dir : Lists.newArrayList(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)){
            BlockPos centerPos = pos.offset(dir.getNormal());
            Block centerBlock = level.getBlockState(centerPos).getBlock();

            if (centerBlock == this){
                spawnGauntlet(centerPos, level);
                break;
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    private void spawnGauntlet(BlockPos centerPos, World level){
        Vector3d spawnPos = VecUtils.asVec3(centerPos).add(new Vector3d(0.5, -0.5, 0.5));
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
    public void animateTick(@Nonnull BlockState state, @Nonnull World level, @Nonnull BlockPos pos, @Nonnull Random random) {
        Vector3d particlePos = VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5)).add(RandomUtils.randVec().normalize());
        Particles.laserChargeParticles.build(particlePos,Vector3d.ZERO);
    }

    public static class Particles {
        public static final ClientParticleBuilder laserChargeParticles = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
                .brightness(BMDParticles.FULL_BRIGHT)
                .color(BMDColors.LASER_RED)
                .colorVariation(0.2);
    }
}
