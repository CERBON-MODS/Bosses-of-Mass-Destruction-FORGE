package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MobPlacementLogic {
    private final ISpawnPosition locationFinder;
    private final IEntityProvider entityProvider;
    private final ISpawnPredicate spawnPredicate;
    private final IMobSpawner spawner;

    public MobPlacementLogic(ISpawnPosition locationFinder, IEntityProvider entityProvider, ISpawnPredicate spawnPredicate, IMobSpawner spawner) {
        this.locationFinder = locationFinder;
        this.entityProvider = entityProvider;
        this.spawnPredicate = spawnPredicate;
        this.spawner = spawner;
    }

    public boolean tryPlacement(int tries) {
        Entity entity = entityProvider.getEntity();
        if (entity == null) return false;

        for (int i = 0; i < tries; i++) {
            Vec3 location = locationFinder.getPos();

            if (spawnPredicate.canSpawn(location, entity)) {
                spawner.spawn(location, entity);
                return true;
            }
        }
        return false;
    }
}

