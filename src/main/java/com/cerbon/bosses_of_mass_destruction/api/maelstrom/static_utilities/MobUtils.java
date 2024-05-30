package com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class MobUtils {
    public static void setPos(Entity entity, Vector3d vec) {
        entity.absMoveTo(vec.x, vec.y, vec.z);
    }

    public static Vector3d eyePos(Entity entity) {
        return entity.getEyePosition(1.0f);
    }

    public static Vector3d lastRenderPos(Entity entity) {
        return new Vector3d(entity.xOld, entity.yOld, entity.zOld);
    }
}
