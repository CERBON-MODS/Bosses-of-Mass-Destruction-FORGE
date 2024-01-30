package com.cerbon.bosses_of_mass_destruction.entity.ai;

import net.minecraft.world.phys.Vec3;

public interface ISteering {
    Vec3 accelerateTo(Vec3 target);
}
