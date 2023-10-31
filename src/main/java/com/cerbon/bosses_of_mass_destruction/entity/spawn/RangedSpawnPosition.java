package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.IRandom;
import net.minecraft.world.phys.Vec3;

public class RangedSpawnPosition implements ISpawnPosition {
    private final Vec3 position;
    private final double minDistance;
    private final double maxDistance;
    private final IRandom random;

    public RangedSpawnPosition(Vec3 position, double minDistance, double maxDistance, IRandom random) {
        this.position = position;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.random = random;
    }

    @Override
    public Vec3 getPos() {
        Vec3 randomOffset = random.getVector();
        Vec3 coercedRandomOffset = randomOffset.normalize()
                .multiply(Math.max(Math.min(randomOffset.length(), maxDistance), minDistance),
                        Math.max(Math.min(randomOffset.length(), maxDistance), minDistance),
                        Math.max(Math.min(randomOffset.length(), maxDistance), minDistance));
        return position.add(coercedRandomOffset);
    }
}

