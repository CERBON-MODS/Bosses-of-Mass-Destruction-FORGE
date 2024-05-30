package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.IRandom;
import net.minecraft.util.math.vector.Vector3d;

public class RangedSpawnPosition implements ISpawnPosition {
    private final Vector3d position;
    private final double minDistance;
    private final double maxDistance;
    private final IRandom random;

    public RangedSpawnPosition(Vector3d position, double minDistance, double maxDistance, IRandom random) {
        this.position = position;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.random = random;
    }

    @Override
    public Vector3d getPos() {
        Vector3d randomOffset = random.getVector();
        Vector3d coercedRandomOffset = randomOffset.normalize().scale(Math.max(Math.min(randomOffset.length(), maxDistance), minDistance));
        return position.add(coercedRandomOffset);
    }
}

