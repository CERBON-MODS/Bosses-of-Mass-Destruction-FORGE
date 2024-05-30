package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.vector.Vector3d;

public class SimpleMobSpawner implements IMobSpawner {
    private final ServerWorld serverLevel;

    public SimpleMobSpawner(ServerWorld serverLevel) {
        this.serverLevel = serverLevel;
    }

    @Override
    public void spawn(Vector3d pos, Entity entity) {
        entity.setPos(pos.x, pos.y, pos.z);
        if (entity instanceof MobEntity) {
            ((MobEntity)entity).finalizeSpawn(
                    serverLevel,
                    serverLevel.getCurrentDifficultyAt(entity.blockPosition()),
                    SpawnReason.MOB_SUMMONED,
                    null,
                    null
            );
        }

        serverLevel.addFreshEntityWithPassengers(entity);

        if (entity instanceof MobEntity) {
            ((MobEntity) entity).spawnAnim();
        }
    }
}

