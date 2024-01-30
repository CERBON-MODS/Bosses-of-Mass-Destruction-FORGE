package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import net.minecraft.world.phys.Vec3;

public class RangedSpawnPosition implements ISpawnPosition {
    private final Vec3 position;
    private final double minDistance;
    private final double maxDistance;

    public RangedSpawnPosition(Vec3 position, double minDistance, double maxDistance) {
        this.position = position;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public Vec3 getPos() {
        Vec3 randomOffset = RandomUtils.randVec();
        Vec3 coercedRandomOffset = randomOffset.normalize().scale(Math.max(Math.min(randomOffset.length(), maxDistance), minDistance));
        return position.add(coercedRandomOffset);
    }
}

