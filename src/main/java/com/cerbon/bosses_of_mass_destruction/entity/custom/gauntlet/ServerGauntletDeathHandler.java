package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.config.mob.GauntletConfig;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.phys.Vec3;

public class ServerGauntletDeathHandler implements IEntityTick<ServerLevel> {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;
    private final GauntletConfig mobConfig;

    public static final int deathAnimationTime = 50;

    public ServerGauntletDeathHandler(GauntletEntity entity, EventScheduler eventScheduler, GauntletConfig mobConfig) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.mobConfig = mobConfig;
    }

    @Override
    public void tick(ServerLevel level) {
        entity.deathTime++;
        if (entity.deathTime == deathAnimationTime){
            level.explode(null, entity.position().x, entity.position().y, entity.position().z, 4.0f, Level.ExplosionInteraction.MOB);
            if (mobConfig.spawnAncientDebrisOnDeath) createLoot(level);
            dropExp();
            entity.remove(Entity.RemovalReason.KILLED);
        }
    }

    private void createLoot(ServerLevel level){
        for (int i = 0; i <= 4; i++){
            Vec3 randomDir = RandomUtils.randVec().normalize();
            int length = 8 - i;
            Vec3 start = entity.position();
            Vec3 end = entity.position().add(randomDir.scale(length));
            int points = length * 2;
            MathUtils.lineCallback(start, end, points, (vec3, point) -> {
                BlockPos blockPos = BlockPos.containing(vec3);
                if (point == points - 1) level.setBlockAndUpdate(blockPos, Blocks.ANCIENT_DEBRIS.defaultBlockState());
                else level.setBlockAndUpdate(blockPos, Blocks.NETHERRACK.defaultBlockState());
            });
        }
        BlockPos chestPos = entity.blockPosition().above();
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 2);
        RandomizableContainerBlockEntity.setLootTable(level, entity.getRandom(), chestPos, new ResourceLocation(BMDConstants.MOD_ID, "chests/gauntlet"));
    }

    private void dropExp(){
        int expTicks = 20;
        int expPerTick = (int) (mobConfig.experienceDrop / (float) expTicks);
        Vec3 pos = entity.position();

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            VanillaCopiesServer.awardExperience(
                                    expPerTick,
                                    pos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).scale(2.0)),
                                    entity.level()
                            );
                        },
                        0,
                        expTicks,
                        () -> false
                )
        );
    }
}
