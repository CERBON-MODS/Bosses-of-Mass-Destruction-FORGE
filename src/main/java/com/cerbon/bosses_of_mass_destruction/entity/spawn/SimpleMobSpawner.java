package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class SimpleMobSpawner implements IMobSpawner {
    private final ServerLevel serverLevel;

    public SimpleMobSpawner(ServerLevel serverWorld) {
        this.serverLevel = serverWorld;
    }

    @Override
    public void spawn(Vec3 pos, Entity entity) {
        entity.setPos(pos);
        if (entity instanceof Mob) {
            ForgeEventFactory.onFinalizeSpawn((Mob) entity, serverLevel,
                    serverLevel.getCurrentDifficultyAt(entity.blockPosition()),
                    MobSpawnType.MOB_SUMMONED,
                    null,
                    null);
        }

        serverLevel.addFreshEntityWithPassengers(entity);

        if (entity instanceof Mob) {
            ((Mob) entity).spawnAnim();
        }
    }
}

