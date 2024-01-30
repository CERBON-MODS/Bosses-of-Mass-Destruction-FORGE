package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.world.phys.Vec3;

public class HorizontalRangedSpawnPosition implements ISpawnPosition {
    private final Vec3 position;
    private final double minDistance;
    private final double maxDistance;

    public HorizontalRangedSpawnPosition(Vec3 position, double minDistance, double maxDistance) {
        this.position = position;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public Vec3 getPos() {
        Vec3 randomOffset = RandomUtils.randVec().normalize();
        Vec3 horizontalAddition = VecUtils.planeProject(randomOffset, VecUtils.yAxis).normalize();
        Vec3 coercedRandomOffset = randomOffset.add(horizontalAddition).normalize().scale(Math.max(Math.min(randomOffset.length(), maxDistance), minDistance));
        return position.add(coercedRandomOffset);
    }
}

