package com.cerbon.bosses_of_mass_destruction.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3d;

public interface Vec3Receiver {
    void clientHandler(ClientWorld level, Vector3d vec3);
}
